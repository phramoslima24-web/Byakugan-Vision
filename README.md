# Byakugan Vision — Forge 1.20.1

Addon visual exclusivo do Byakugan para Narutoto 0.4.2.

## O que faz
- Quando `if_byakugan == 1.0` no capability do Narutoto, aplica filtro de tela em preto e branco.
- Desenha pontos luminosos de chakra em entidades vivas próximas, inclusive através de blocos, até 64 blocos.
- Ao desativar o Byakugan, remove o filtro.

## Compilação
Requer Java 17 e ForgeGradle. No GitHub, envie o conteúdo deste diretório para a raiz do repositório e execute Actions → Build Byakugan Vision. O JAR estará no artefato `Byakugan-Vision-1.20.1`.

Este projeto não altera transplantes, Sharingan, inventário ou renderização dos olhos. O estado de ativação é lido diretamente de `NarutotoModVariables.if_byakugan`.
