package ru.mumu.bot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mumu.bot.constants.Constants;
import ru.mumu.bot.db.IDBOperations;
import ru.mumu.bot.redis.RedisEntity;
import ru.mumu.bot.utils.BotHelper;
import ru.mumu.bot.utils.Utils;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MumuBot extends TelegramLongPollingBot {

    private static final Logger logger = Logger.getLogger(MumuBot.class.getSimpleName());
    private IDBOperations idbOperations;
    private final String botName;
    private final String botToken;

    public MumuBot() {
        this.botName = RedisEntity.getInstance().getElement("mumu_botName");
        this.botToken = RedisEntity.getInstance().getElement("mumu_botToken");
    }

    @Inject
    public MumuBot(IDBOperations idbOperations) {
        this.idbOperations = idbOperations;
        this.botName = RedisEntity.getInstance().getElement("mumu_botName");
        this.botToken = RedisEntity.getInstance().getElement("mumu_botToken");
    }

    @Override
    public void onUpdateReceived(Update update) {

        Calendar calendar = Calendar.getInstance();
        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        logger.log(Level.INFO, "Today is {0}", dayOfWeek);

        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            logger.log(Level.INFO, "FirstName: {0}, LastName: {1}, UserName: {2} \n" +
                            "UserId: {3}, ChatId: {4}, CommandInput: {5}",
                    new Object[]{message.getFrom().getFirstName(),
                            message.getFrom().getLastName(),
                            message.getFrom().getUserName(),
                            message.getFrom().getId(),
                            message.getChatId(),
                            message.getText()});

            //If user wants to get menu in holiday or weekend, he will get message, that menu is only from Monday to Friday
            // Besides commands: ADDRESSES, HELP and START
            String holidayInfo = BotHelper.checkDayForHoliday(message.getText(), dayOfWeek);
            if (holidayInfo != null) {
                sendMessage(message, holidayInfo);
                return;
            }

            String info;
            if (Constants.getDaysOfWeek().contains(message.getText())) {
                info = idbOperations.selectDataFromDB(message.getText());
                if (info == null) {
                    info = Constants.CACHING_MESSAGE_FOR_USER;
                }
            } else {
                info = BotHelper.getInfo(message.getText());
            }

            sendMessage(message, info);
        }
    }

    private void sendMessage(Message message, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Utils.getTextForUser(message, textMessage));
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            logger.log(Level.SEVERE, "TelegramApiException: ", ex);
        }
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }
}
