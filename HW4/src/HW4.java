import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by Madstein on 2016-10-15.
 */
public class HW4 {

    public static String makeBETWEEN(String kind, String min, String max) {
        //return "(" + kind + " BETWEEN " + Double.toString(min) + " AND "+ Double.toString(max) + ")";
        return kind + " >= " + min + " AND "+ kind + " <= " + max;
    }

    public static void main(String[] args)  {
        Connection con = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        String[] insertThis;
        String queryString;
        ResultSet rs;
        int i;
        try {
            InputStreamReader r = new InputStreamReader(System.in);
            BufferedReader b = new BufferedReader(r);
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection( "jdbc:oracle:thin:@dbclick.kaist.ac.kr:1521:orcl", "s20140602", "20140602");
            stmt = con.createStatement();

            // Problem 1
            String maximumPrice;
            String[] minimumSpec;
            System.out.print("maximum price : ");
            maximumPrice = b.readLine();
            System.out.print("minimum values (in the form of speed ram hd screen) : ");
            minimumSpec = b.readLine().split(" ");
            queryString = "SELECT maker, product.model, speed, ram, hd, screen, price FROM product, laptop WHERE price <= ? AND speed >= ? AND ram >= ? AND hd >= ? AND screen >= ?";
            pstmt = con.prepareStatement(queryString);
            pstmt.setString(1, maximumPrice);
            pstmt.setString(2, minimumSpec[0]);
            pstmt.setString(3, minimumSpec[1]);
            pstmt.setString(4, minimumSpec[2]);
            pstmt.setString(5, minimumSpec[3]);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.print("maker : ");
                System.out.print(rs.getString(1) + ", ");
                System.out.print("model : ");
                System.out.print(rs.getString(2) + ", ");
                System.out.print("speed : ");
                System.out.print(rs.getString(3) + ", ");
                System.out.print("ram : ");
                System.out.print(rs.getString(4) + ", ");
                System.out.print("hd : ");
                System.out.print(rs.getString(5) + ", ");
                System.out.print("screen : ");
                System.out.print(rs.getString(6) + ", ");
                System.out.print("price : ");
                System.out.print(rs.getString(7));
                System.out.println("");
            }

            // Problem 2
            System.out.println("Problem 2");
            System.out.println("type tuple in format : maker model speed ram hd price");
            insertThis = b.readLine().split(" "); // maker, model, speed, ram, hd, price : same as sample data table attribute order.

            System.out.println("deleting from product...");
            queryString = "DELETE FROM product WHERE model = ?";
            pstmt = con.prepareStatement(queryString);
            pstmt.setInt(1, Integer.parseInt(insertThis[1]));
            pstmt.executeUpdate();
            System.out.println("success");

            System.out.println("deleting from pc...");
            queryString = "DELETE FROM pc WHERE model = ?";
            pstmt = con.prepareStatement(queryString);
            pstmt.setString(1, insertThis[1]);
            pstmt.executeUpdate();
            System.out.println("success");

            rs = stmt.executeQuery("select * from product where model = " + insertThis[1]);
            if (rs.next()) {
                System.out.println("WARNING : There already exists tuple with that model number.");
            } else {

                System.out.println("inserting into product...");
                queryString = "INSERT INTO product VALUES (?, ?, 'pc')";
                pstmt = con.prepareStatement(queryString);
                pstmt.setString(1, insertThis[0]);
                pstmt.setString(2, insertThis[1]);
                pstmt.executeUpdate();
                System.out.println("success");

                System.out.println("inserting into pc...");
                queryString = "INSERT INTO pc (model, speed, ram, hd, price) VALUES (?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(queryString);
                pstmt.setString(1, insertThis[1]);
                pstmt.setString(2, insertThis[2]);
                pstmt.setString(3, insertThis[3]);
                pstmt.setString(4, insertThis[4]);
                pstmt.setString(5, insertThis[5]);
                pstmt.executeUpdate();
                System.out.println("success");

            }
            rs = stmt.executeQuery("SELECT * FROM product");
            System.out.println("MAKER\tMODEL\t\tTYPE");
            while (rs.next()) {
                String maker = rs.getString(1);
                String model = rs.getString(2);
                String type = rs.getString(3);
                System.out.println(maker + "\t\t" + model + "\t\t" + type);
            }
            rs = stmt.executeQuery("SELECT * FROM pc");
            System.out.println("");
            System.out.println("MODEL SPEED RAM   HD    PRICE");
            while (rs.next()) {
                String model = rs.getString(1);
                String speed = rs.getString(2);
                String ram = rs.getString(3);
                String hd = rs.getString(4);
                String price = rs.getString(5);
                System.out.format("%-5s %-5s %-5s %-5s %-5s\n", model, speed, ram, hd, price);
            }

            System.out.println("deleting from product...");
            queryString = "DELETE FROM product WHERE model = ?";
            pstmt = con.prepareStatement(queryString);
            pstmt.setString(1, insertThis[1]);
            pstmt.executeUpdate();
            System.out.println("success");

            System.out.println("deleting from pc...");
            queryString = "DELETE FROM pc WHERE model = ?";
            pstmt = con.prepareStatement(queryString);
            pstmt.setString(1, insertThis[1]);
            pstmt.executeUpdate();
            System.out.println("success");

            // Problem 3
            System.out.println("Problem 3");
            System.out.print("Enter desired price : ");
            int userPrice = Integer.parseInt(b.readLine());
            int diffMin = 100000;
            int modelMin = 0;
            String[] pcInfo = new String[3];
            rs = stmt.executeQuery("SELECT * FROM pc");
            while (rs.next()) {
                int priceTemp = Integer.parseInt(rs.getString(5));
                int diffTemp = abs(userPrice - priceTemp);
                if (diffTemp < diffMin) {
                    diffMin = diffTemp;
                    modelMin = Integer.parseInt(rs.getString(1));
                }
            }
            queryString = "select maker from product where model = " + String.valueOf(modelMin);
            rs = stmt.executeQuery(queryString);
            while(rs.next()) {
                pcInfo[0] = rs.getString(1);
            }
            queryString = "select model, ram from pc where model = " + String.valueOf(modelMin);
            rs = stmt.executeQuery(queryString);
            while(rs.next()) {
                pcInfo[1] = rs.getString(1);
                pcInfo[2] = rs.getString(2);
            }
            System.out.println("MAKER MODEL RAM");
            System.out.format("%-5s %-5s %-5s\n", pcInfo[0], pcInfo[1], pcInfo[2]);

            // Problem 4
            System.out.println("");
            System.out.println("Problem 4");
            System.out.print("Enter maker : ");
            ArrayList<String> modelList = new ArrayList<>();
            ArrayList<String> typeList = new ArrayList<>();
            String userMaker = b.readLine();
            queryString = "select * from product where maker = " + "'" + userMaker + "'";
            rs = stmt.executeQuery(queryString);
            while(rs.next()) {
                modelList.add(rs.getString(2));
                typeList.add(rs.getString(3));
            }
            for (int k=0; k < modelList.size(); k++) {
                String typeTemp = typeList.get(k);
                String modelTemp = modelList.get(k);
                queryString = "SELECT * FROM " + typeTemp + " WHERE model = " + modelTemp;
                ResultSet rsTemp = stmt.executeQuery(queryString);

                if (typeList.get(k).equals("pc")) {
                    if (rsTemp.next()) {
                        System.out.print("MODEL : " + modelTemp + ", ");
                        System.out.print("TYPE(PRODUCT) : " + typeTemp + ", ");
                        System.out.print("SPEED : " + rsTemp.getString(2) + ", ");
                        System.out.print("RAM : " + rsTemp.getString(3) + ", ");
                        System.out.print("HD : " + rsTemp.getString(4) + ", ");
                        System.out.print("PRICE : " + rsTemp.getString(5) + ", ");
                    }
                } else if (typeTemp.equals("laptop")) {
                    if (rsTemp.next()) {
                        System.out.print("MODEL : " + modelTemp + ", ");
                        System.out.print("TYPE(PRODUCT) : " + typeTemp + ", ");
                        System.out.print("SPEED : " + rsTemp.getString(2) + ", ");
                        System.out.print("RAM : " + rsTemp.getString(3) + ", ");
                        System.out.print("HD : " + rsTemp.getString(4) + ", ");
                        System.out.print("SCREEN : " + rsTemp.getString(5) + ", ");
                        System.out.print("PRICE : " + rsTemp.getString(6) + ", ");
                    }
                } else {
                    if (rsTemp.next()) {
                        System.out.print("MODEL : " + modelTemp + ", ");
                        System.out.print("TYPE(PRODUCT) : " + typeTemp + ", ");
                        System.out.print("COLOR : " + rsTemp.getString(2) + ", ");
                        System.out.print("TYPE(PRINTER) : " + rsTemp.getString(3) + ", ");
                        System.out.print("PRICE : " + rsTemp.getString(4) + ", ");
                    }
                }
                System.out.println("");
            }

            // Problem 5
            System.out.println("");
            System.out.println("Problem 5");
            System.out.println("Enter total budget and minimum speed in format : budget speed");
            String[] userCondition; // order : budget, minimum speed
            userCondition = b.readLine().split(" ");
            int minPricePC = 100000000;
            int minPricePrinter = 10000000;
            String cheapestPC = null; // model number of the cheapest PC.
            String cheapestPrinter = null; // model number of the possible cheapest (Color) Printer
            queryString = "SELECT * FROM pc WHERE speed >= " + userCondition[1];
            rs = stmt.executeQuery(queryString);
            while(rs.next()) {
                if (Integer.parseInt(rs.getString(5)) < minPricePC) {
                    minPricePC = Integer.parseInt(rs.getString(5));
                    cheapestPC = rs.getString(1);
                }
            }

            queryString = "SELECT * FROM printer WHERE color = 1";
            rs = stmt.executeQuery(queryString);
            while(rs.next()) {
                if (Integer.parseInt(rs.getString(4)) < minPricePrinter) {
                    minPricePrinter = Integer.parseInt(rs.getString(4));
                    cheapestPrinter = rs.getString(1);
                }
            }

            if (minPricePC + minPricePrinter < Integer.parseInt(userCondition[0])) {
                System.out.println("PC : " + cheapestPC + ", Printer : " + cheapestPrinter);
            } else {
                queryString = "SELECT * FROM printer WHERE color = 0";
                rs = stmt.executeQuery(queryString);
                while(rs.next()) {
                    if (Integer.parseInt(rs.getString(4)) < minPricePrinter) {
                        minPricePrinter = Integer.parseInt(rs.getString(4));
                        cheapestPrinter = rs.getString(1);
                    }
                }
                if (minPricePC + minPricePrinter < Integer.parseInt(userCondition[0])) {
                    System.out.println("PC : " + cheapestPC + ", Printer : " + cheapestPrinter);
                } else {
                    System.out.println("No such pc and printer");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (Exception e) { }
        }

    }

}
