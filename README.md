# Telegram lunchmumubot

### ❗️Important information

- ️️️️❗️Now the bot doesn't work, because the `mu-mu cafe` removed lunches from site </br>
- ❗️And the `Grand Victoria cafe` site doesn't work anymore

### About bot

It's unofficial Telegram bot for cafe [mumu](https://www.cafemumu.ru). <br/>
Bot shows lunches on weekdays by command.
By the way! Bot is broadcasting menu for users everyday at 11:05 AM.

And bonus - bot shows lunches in [Grand Victoria cafe](http://restaurantgrandvictoria.ru).

Find the bot in Telegram: [@lunchmumubot](https://telegram.me/lunchmumubot)

## Instruction for build and deploy

1. Download or clone [repository](https://github.com/schepach/telegram-mumu-bot.git)
2. Run the [Redis store](https://redis.io/)
3. Set your `mumu_botName` and `mumu_botToken` values in Redis store
4. Set the value `mumu_admin` for use broadcasting functionality
5. Run the [Wildfly Application Server](https://www.wildfly.org/)
6. Configure `SQLite` datasource on Wildfly ([instruction](https://t.me/alexeywrites/30) by Russian)
7. Connect to database and create table `menuItems` (See the file `queries.sql` in `resources` folder)
8. Build project with `maven` (`mvn clean package`)
9. Deploy `.ear` file from `target` folder to Wildfly Application Server
10. Enjoy!