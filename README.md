# Snorlax [![Build Status](https://travis-ci.org/igoticecream/Snorlax.svg?branch=master)](https://travis-ci.org/igoticecream/Snorlax) [![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A9PPGNDJEC33E)
Check pokemons stats. [Available through Xposed repository](http://repo.xposed.info/module/com.icecream.snorlax)
  
This module acts as a [man in the middle](https://en.wikipedia.org/wiki/Man-in-the-middle_attack) on Pokémon Go's communications with the server, and allows us to add extra features to the game.

Features:
- Prevent PokemonGo from detecting GPS is being spoofed (similar to Mock Mock Locations)
- Show pokemon stats on the inventory
- Show pokemon stats as a system notification when you encounter them: IV's, HP, CP, level, moves, catch probability
- Show catch results: Success, missed, flee, etc.

Pokemon stats custom formats:
- Nickname
  - %NICK% : Shows pokemon full name. %NICK% -> Snorlax
  - %NICK.#% : Trims pokemon name by #. %NICK.3% -> Sno
- Level
  - %LVL% : Shows pokemon level with one decimal. %LVL% -> 2.5
  - %LVL.#% : Round up and trims pokemon level by # decimals. %LVL.0% -> 3. %LVL.2% -> 2.50
  - %LVLP% : Same as %LVL% but add leading zeros. %LVLP% -> 02.5
  - %LVLP.#% : Same as %LVL.#% but add leading zeros. %LVLP.0% -> 03. %LVLP.2% -> 02.50
- Iv
  - %IV% : Shows pokemon iv with one decimal. %IV% -> 6.7
  - %IV.#% : Round up and trims pokemon iv by # decimals. %IV.0% -> 7. %IV.2% -> 6.67
  - %IVP% : Same as %IV% but add leading zeros. %IVP% -> 006.7
  - %IVP.#% : Same as %IV.#% but add leading zeros. %IVP.0% -> 007. %IVP.2% -> 006.67
- Attack
  - %ATT% : Shows pokemon attack. %ATT% -> 1
  - %ATTP% : Shows pokemon attack with leading zero. %ATTP% -> 01
  - %ATTH% : Shows pokemon attack with hexadecimal format. %ATTH% (15) -> F
- Defense
  - %DEF% : Shows pokemon defense. %DEF% -> 1
  - %DEFP% : Shows pokemon defense with leading zero. %DEFP% -> 01
  - %DEFH% : Shows pokemon defense with hexadecimal format. %DEFH% (15) -> F
- Stamina
  - %STA% : Shows pokemon stamina. %STA% -> 1
  - %STAP% : Shows pokemon stamina with leading zero. %STAP% -> 01
  - %STAH% : Shows pokemon stamina with hexadecimal format. %STAH% (15) -> F

Note: Snorlax wont format other that the above commands. This means that you could add a '%' next to the Iv like this: %IV%% -> 6.7%

## Libraries and tools
- Android's support libraries
- Reactive extensions: [RxJava](https://github.com/ReactiveX/RxJava), [RxAndroid](https://github.com/ReactiveX/RxAndroid), [RxBinding](https://github.com/JakeWharton/RxBinding) and [RxRelay](https://github.com/JakeWharton/RxRelay)
- Dependency injector: [Dagger 2](http://google.github.io/dagger/)
- Android's views binding: [Butterknife](https://github.com/JakeWharton/butterknife)
- Logging utility: [Timber](https://github.com/JakeWharton/timber)
- Generated immutable value classes: [AutoValue](https://github.com/google/auto/tree/master/value)
- [Xposed framework](https://github.com/rovo89/XposedBridge)
- [Protobuf](https://github.com/google/protobuf-gradle-plugin)

## Build requirements
- JDK 1.8
- [Android SDK](http://developer.android.com/sdk/index.html)
- Android N [(API 24) ](http://developer.android.com/tools/revisions/platforms.html)
- Latest Android SDK Tools and build tools
- Your device must be rooted and the xposed framekwork must be installed. You can download the xposed framework [here](http://repo.xposed.info/module/de.robv.android.xposed.installer).

## Build source
Clone the repository (you must have Git installed)
```
git clone https://github.com/igoticecream/Snorlax.git
cd .\Snorlax\
git submodule update --init
```

Build
```
.\gradlew build
```

To build a release APK
```
.\gradlew assembleRelease
```

To install directly to the phone connected via ADB
```
.\gradlew installRelease
```

## Contributing
  - Fork it!
  - Create your feature branch: `git checkout -b my-new-feature`
  - Commit your changes: `git commit -am 'Useful information about your new features'`
  - Push to the branch: `git push origin my-new-feature`
  - Submit a pull request on the `dev` (all pull request on master branch will be rejected)

## Special thanks
Snorlax would not have been possible without:
- [chuparCh0pper](https://github.com/chuparCh0pper/PoGoIV_xposed) for his awesome [module](https://github.com/chuparCh0pper/PoGoIV_xposed) which Snorlax is inspired on
- [ELynx](https://github.com/ELynx) for the method to MITM PokemonGo communications, check his [repo](https://github.com/ELynx/pokemon-go-xposed-mitm) too
- And last but not least, [AeonLucid](https://github.com/AeonLucid) for the [PokemonGo's protos](https://github.com/AeonLucid/POGOProtos)

## Donation
I hope you are liking Snorlax!

If you do, you can support this project to keep coming with new features by buying me a cup of coffee (so i can code by night) or a BigMac at McDonalds. Click the link below and thanks you!

[Donate](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A9PPGNDJEC33E)

## License
    Copyright (c) 2016. Pedro Diaz <igoticecream@gmail.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
