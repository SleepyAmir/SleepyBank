package bank.app.model.repository.utils;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class DatabaseMaker {
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

    }
}
