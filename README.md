# Telegram lunchmumubot

About bot
=====================
This is unofficial Telegram bot for cafe [mumu](https://www.cafemumu.ru). <br/>
This bot shows lunches in cafe on weekdays by command.
By the way! Bot is broadcasting info about lunches to current day from 11am to 12am.

And bonus - this bot shows lunches in [Grand Victoria cafe](http://restaurantgrandvictoria.ru).

Search it in Telegram: [@lunchmumubot](https://telegram.me/lunchmumubot)

Instruction for build and deploy
=====================
1. Download or clone [repository](https://github.com/schepach/TelegramMumuBot.git)
2. Set your botName and botToken in `MumuBot.java` class
3. Build project with `maven` (`mvn clean package`)
4. Go to the `target` folder and deploy `.ear` file. (For example, on Wildfly Application Server)
5. Enjoy!