package cz.ondrejpittl.semestralka.factories;

import java.text.ParseException;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class DBQueryFactory {

    private static StringBuilder sb = new StringBuilder();


    public static String createTable(String tableName, String[][] args){
        int colCount = args.length;

        clearStringBuilder();

        sb.append("CREATE TABLE IF NOT EXISTS " + tableName + "(");

        for(int i = 0; i < colCount; i++) {
            sb.append(args[i][0] + " " + args[i][1]);
            if(i < colCount - 1) sb.append(",");
        }

        sb.append(")");

        return sb.toString();
    }

    public static String querySelectAll(String tableName){
        return "select * from " + tableName;
    }

    public static String querySelect(String tableName, String[][] wheres, String[][] orderBy){
        //"select * from " + TABLE_NAME + " where id="+id+""

        clearStringBuilder();
        sb.append("SELECT * FROM " + tableName);

        buildWhere(wheres);
        buildOrderBy(orderBy);
        return sb.toString();
    }

    public static String querySelectOne(String tableName, String[][] wheres, String[][] orderBy){
        String query = DBQueryFactory.querySelect(tableName, wheres, orderBy);
        bildLimit(1);
        return query;
    }

    public static String querySelectDetailed(String tableCols, String tables, String[][] wheres, String[][] orderBy, int limit) {
        clearStringBuilder();
        sb.append("SELECT ");
        sb.append(tableCols);
        sb.append(" FROM ");
        sb.append(tables);
        buildWhere(wheres);
        buildOrderBy(orderBy);
        bildLimit(limit);
        return sb.toString();
    }

    private static void buildOrderBy(String[][] orderBy){
        if(orderBy == null || orderBy.length <= 0) return;

        sb.append(" ORDER BY ");

        for(int o = 0; o < orderBy.length; o++) {
            sb.append(orderBy[o][0] + " " + orderBy[o][1]);
            if(o < orderBy.length - 1) sb.append(", ");
        }
    }

    private static void buildWhere(String[][] wheres){
        if(wheres == null || wheres.length <= 0) return;

        sb.append(" WHERE ");

        for(int w = 0; w < wheres.length; w++) {
            sb.append(wheres[w][0] + " " + wheres[w][1] + " '" + wheres[w][2] + "'");
            if(w < wheres.length - 1) sb.append(" AND ");
        }
    }

    private static void bildLimit(int limit) {
        if(limit > 0)
            sb.append(" LIMIT " + limit);
    }


    public static String queryDeleteWhereClause(String[][] wheres){
        int whereCount = wheres.length;
        clearStringBuilder();

        for(int i = 0; i < whereCount; i++) {
            sb.append(wheres[i][0] + " " + wheres[i][1] + " ? ");
            if(i < whereCount - 1) sb.append("AND ");
        }

        return sb.toString();
    }

    public static String[] queryDeleteWhereArgs(String[][] wheres){
        int whereCount = wheres.length;
        String[] args = new String[whereCount];

        for(int i = 0; i < whereCount; i++) {
            args[i] = String.valueOf(wheres[i][2]);
        }

        return args;
    }



    /*

    private static boolean isNumeric(String str){
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException ex) {
            return false;
        }
        return true;
    }

    */


    private static void clearStringBuilder(){
        sb.setLength(0);
    }

}
