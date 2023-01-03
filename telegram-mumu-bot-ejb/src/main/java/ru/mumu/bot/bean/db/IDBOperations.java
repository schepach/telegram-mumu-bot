package ru.mumu.bot.bean.db;

public interface IDBOperations {

    String selectDataFromDB(String daysOfWeek);

    void insertDataToDB(String daysOfWeek, String menu);

    void updateDataToDB(String daysOfWeek, String menu);

    void deleteDataFromDB();

}
