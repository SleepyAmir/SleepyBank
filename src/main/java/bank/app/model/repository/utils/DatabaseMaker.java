package bank.app.model.repository.utils;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class DatabaseMaker {
    private static Logger log = Logger.getLogger(DatabaseMaker.class);

    public static void createDatabase() throws Exception {
        File file = new File("./src/main/java/bank/app/model/repository/dbsql/bank_db.sql");

        Scanner scanner = new Scanner(file);
        String lines = "";
        while(scanner.hasNextLine()) {
            lines += scanner.nextLine();
        }

        for (String sqlCommand : lines.split(";")) {
            PreparedStatement preparedStatement =  ConnectionProvider.getConnectionProvider().getConnection().prepareStatement(sqlCommand);
            preparedStatement.execute();
        }
        log.info("Database Created");
    }
}
