# Sharingan Evolution Addon — Forge 1.20.1

## Transplante ocular independente

A interface agora oferece operações explícitas por lado: retirar, implantar Sharingan ou implantar Byakugan no olho esquerdo e, separadamente, no direito. Isso permite combinações como Sharingan/Byakugan, olho normal/Sharingan, Byakugan/normal e ambos iguais.

As escolhas ficam salvas no PersistentData do jogador. As flags do Narutoto são sincronizadas como compatibilidade por tipo presente (o Narutoto base representa dojutsu por jogador/clã, não por olho).

## Limitação importante de renderização

O Narutoto 0.4.2 usa procedimentos próprios de renderização de olhos. Suas variáveis públicas representam o dojutsu do jogador e não fornecem, por si só, um seletor independente para olho esquerdo/direito. Portanto este addon corrige a lógica e a interface de transplante, mas a aparência mista de cada olho exige uma integração específica no renderer/modelo do Narutoto. Não se deve considerar a renderização independente concluída apenas pela compilação deste addon.

## Build

Requer Java 17, Forge 1.20.1-47.4.10 e `libs/narutoto-0.4.2-forge-1.20.1.jar`.

No GitHub, o workflow `.github/workflows/build.yml` executa `gradle --no-daemon clean build` e publica `build/libs/*.jar` como artefato.


## Retirada individual dos olhos

- `P`: retira o olho esquerdo e entrega o item correspondente do Narutoto ao inventário.
- `Shift + P`: retira o olho direito e entrega o item correspondente do Narutoto ao inventário.
- Os itens usados são `sharingan_eyeball` e `byakugan_eyeball`, aproveitando os ícones/texturas originais do Narutoto 0.4.2.
- A tela de transplante continua permitindo implantar Sharingan ou Byakugan separadamente em cada lado.

A compilação precisa ser executada pelo Gradle 8.8/Java 17 (workflow do GitHub Actions), pois este ambiente não possui Gradle instalado.
