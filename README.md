# DnD
Support scripts for DM Stuff.

## Current features

### Creature Generator

- Can Generate varieng creatures and print them into files
- Use CSV files and Rainbow Plugin (VsCode) to have fancy way of viewing it
- takes input from .txt files (Not setup for all variables, some still just have long String[])

### Quest Generator

- Generates procedual quest in the simplest way possible
- uses String Arrays as input
- Always generate multible quest since most of them are really stupid
- Quests are in the form of: Do X, at Location y and get reward Z

### Radio/ Audio Player

- Set Frequency to different channels
- Dial Frequeny using the "Q" and "E" button
- Can play different audio clips and static noise
- Starts playback at position based on faked internal clock (the clock starts when the programm starts and always counts up)
**Now with smart clip player!!!** 

### Player Generation

- Interactive programm to create a new player
- Writes the output to a markdown file using md formating
- Plan is to have a second script convert the md directly to pdf, currently broken
- Editing exisiting players is currently not possible but planned

## Markdown Notes

- Bunch of stuff from another folder that i moved over during a cleanup. The Stuff is not relevant for the project
- Character Stats --> Stats for Players
- Enemies --> Enemies specific to my Stalker Campaign
- Plants_DE --> Idea to generate different plants with effects like poisen, nutrients (Currently in German)
- Weopens --> Modern Day Weopens

## Planned Features

### World Tracker

- Track Player Stats between sessions
- ...
- Basicly just store all the data that the dm might forgett during sessions

### Zeus

- Name inspired by ARMA III
- Set weather based on region, previeus weather, wind...
- Controll/ Remember World events like earthquackes, floods, hurricanes, anomalie outbreaks, crashsites, milita conflicts....

### Online Tabletop

- Will NOT be in Java but rather in Godot 4
- I can´t setup a save server at the moment so it will run locally and use port forwarding
- Recommandet tools for forwarding:
    - Radmin VPN for Windows
    - ZeroTier for Linux (radmin doesnt run on linux)
- It will be just a table where people can move images that have been preloadet