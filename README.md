# Detector de Queda

Aplicativo Android que analisa uma foto e indica se a pessoa nela está em uma postura que sugere queda. A foto pode ser tirada na hora ou escolhida da galeria. O app localiza o corpo com visão computacional, mede a inclinação do tronco e mostra o resultado sobre a própria imagem.

O projeto nasceu como um micro-projeto de mobile para praticar visão computacional no celular: usar um modelo de pose já treinado, tratar os pontos que ele devolve e transformá-los em uma decisão simples e explicável.

## O que o app faz

1. Você toca em **Tirar Foto** ou **Galeria**.
2. O app encontra a pessoa na imagem e marca 33 pontos do corpo (pontos vermelhos).
3. Uma linha verde liga o centro dos ombros ao centro dos quadris, representando o tronco.
4. Abaixo da imagem aparecem o **ângulo do tronco** e uma classificação:

| Cor | Resultado | Significado |
|---|---|---|
| Verde | NORMAL | Tronco próximo da vertical |
| Laranja | ATENÇÃO | Tronco bastante inclinado |
| Vermelho | POSSÍVEL QUEDA | Tronco próximo da horizontal |

Se não houver ninguém na foto, o app avisa "Nenhuma pessoa detectada".

Tudo roda no próprio aparelho: o modelo de pose vem dentro do app, então não precisa de internet. O app também não pede nenhuma permissão, porque a foto é tirada pelo app de câmera do sistema.

## Como funciona o cálculo

A ideia é medir o quanto o tronco está inclinado em relação à vertical. Em pé, o ângulo fica perto de 0°. Deitado, perto de 90°.

1. **Pontos usados.** Dos 33 pontos que o MediaPipe devolve, uso quatro: ombro esquerdo (11), ombro direito (12), quadril esquerdo (23) e quadril direito (24).
2. **Centros.** Calculo o ponto médio dos ombros e o ponto médio dos quadris. A linha entre eles é o tronco, e usar a média deixa o resultado mais estável do que olhar um lado só.
3. **Deslocamento.** `dx` é quanto o quadril está deslocado para o lado em relação aos ombros, e `dy` é quanto ele está abaixo.
4. **Correção da escala.** O MediaPipe devolve coordenadas entre 0 e 1, com `x` dividido pela largura da foto e `y` pela altura. Em fotos retangulares as duas unidades são diferentes, o que distorce o ângulo. Por isso multiplico `dx` por `largura / altura` antes de continuar.
5. **Ângulo.** `ângulo = atan2(|dx|, |dy|)`, convertido para graus. O valor absoluto faz inclinar para a esquerda e para a direita dar o mesmo resultado.
6. **Classificação.**

| Ângulo do tronco | Classificação |
|---|---|
| até 45° | NORMAL |
| de 45° a 65° | ATENÇÃO |
| acima de 65° | POSSÍVEL QUEDA |

Os limiares de 45° e 65° são constantes no topo de `FallDetector.kt` e foram escolhidos de forma prática. Podem ser ajustados para deixar o app mais ou menos sensível.

## Estrutura do projeto

O código está separado em três camadas, para que cada parte tenha uma única responsabilidade. A `MainActivity` ficou só com a configuração do tema e a ligação com o ViewModel.

```
app/src/main/
├── assets/
│   └── pose_landmarker_lite.task      modelo de pose do MediaPipe
└── java/com/example/detectordequeda/
    ├── data/                          lida com o "mundo de fora": imagem e MediaPipe
    │   ├── FallRepository.kt          junta carregar imagem + detectar pose + analisar
    │   ├── image/ImageLoader.kt       lê a foto da galeria e converte para o formato do MediaPipe
    │   └── pose/PoseDetector.kt       roda o modelo e devolve os 33 pontos
    ├── domain/                        regras do app, sem depender de Android
    │   ├── FallDetector.kt            o cálculo do ângulo e a classificação
    │   └── model/                     Landmark, FallResult, PoseAnalysis
    └── ui/                            tudo que aparece na tela
        ├── MainActivity.kt
        ├── FallDetectorScreen.kt      botões, imagem e resultado
        ├── FallDetectorViewModel.kt   coordena o processamento e guarda o estado
        ├── FallDetectorUiState.kt     o que a tela precisa mostrar
        └── components/                PoseOverlayImage (pontos e linha) e ResultSection
```

O caminho de uma foto é: botão na tela → `ViewModel` → `FallRepository` → `PoseDetector` (pontos) → `FallDetector` (ângulo e classificação) → de volta ao `ViewModel` → tela atualizada.

## Como desenvolvi

1. **Primeira versão.** Comecei com a tela, o acesso ao MediaPipe e o cálculo juntos em poucos arquivos, apenas para validar a ideia.
2. **Reestruturação.** Separei o código em `ui`, `data` e `domain`. O MediaPipe passou a ficar isolado em `PoseDetector`, que converte os pontos para uma classe própria (`Landmark`). Assim a regra de detecção não depende da biblioteca e poderia ser testada sem o Android.
3. **Estado e desempenho.** Criei um `ViewModel` com um estado único (`FallDetectorUiState`). A detecção roda fora da thread principal, com indicador de carregamento, e o detector é fechado quando o ViewModel é destruído.
4. **Ajustes.** Corrigi o desenho da imagem, que aparecia pequena e desalinhada dos pontos, e corrigi a distorção do ângulo em fotos retangulares usando a proporção da imagem.

## Tecnologias

- Kotlin 2.2.10
- Jetpack Compose (BOM 2026.02.01) e Material 3
- MediaPipe Tasks Vision 0.10.14, com o modelo Pose Landmarker (lite)
- Android Gradle Plugin 9.4.1
- Android 7.0 ou superior (`minSdk` 24)

## Como executar

**Instalando o APK (sem Android Studio):**

1. Baixe o arquivo em [`apk/app-debug.apk`](apk/DetectorDeQueda.apk).
2. Copie para o celular e abra o arquivo. Se o Android pedir, permita a instalação de apps de fontes desconhecidas.
3. Abra o app, toque em **Tirar Foto** ou **Galeria** e escolha uma foto com uma pessoa visível.

**Rodando pelo código:**

1. Clone o repositório e abra a pasta no Android Studio.
2. Aguarde o sync do Gradle terminar.
3. Conecte um celular com depuração USB ativa, ou abra um emulador, e clique em **Run**.

Para gerar o APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**. O arquivo fica em `app-debug.apk`.

## Limitações

- O app analisa **uma foto**, não um movimento. Ele não sabe se a pessoa caiu ou se deitou de propósito, por isso o resultado é "possível queda". Uma pessoa deitada na cama ou no sofá pode ser classificada como queda.
- Alguém abaixado para pegar um objeto também tem o tronco inclinado e pode gerar alarme falso.
- O cálculo usa apenas o tronco em 2D. Se ombros ou quadris estiverem fora da foto, o MediaPipe estima a posição deles e o resultado perde confiabilidade.
- A foto tirada pelo botão **Tirar Foto** é uma miniatura de baixa resolução, o que pode prejudicar a detecção. Fotos escolhidas da galeria funcionam melhor.
- Em aparelhos com Android 7 a 9, fotos da galeria podem vir giradas, o que também afeta o resultado.
- Os limiares de 45° e 65° não foram calibrados com um conjunto grande de fotos.

## Possíveis evoluções

- Analisar vídeo da câmera ao vivo e observar a mudança de postura ao longo de poucos segundos, o que ajudaria a diferenciar uma queda de uma pessoa que simplesmente se deitou.
- Usar mais pontos do corpo, como joelhos e cabeça, para reduzir alarmes falsos.
- Calibrar os limiares com fotos reais de diferentes posturas.