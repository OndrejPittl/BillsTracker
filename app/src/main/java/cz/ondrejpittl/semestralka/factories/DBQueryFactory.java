package cz.ondrejpittl.semestralka.factories;

/**
 * Created by OndrejPittl on 31.03.16.
 */
public class DBQueryFactory {

    /**
     * Stores a query being built.
     */
    private static StringBuilder sb = new StringBuilder();


    /**
     * Creates a table create query.
     * @param tableName a name of a table
     * @param args      arguments
     * @return  a result query
     */
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

    /**
     * Creates a records select query.
     * @param tableName a name of a table
     * @return          a result query
     */
    public static String querySelectAll(String tableName){
        return "select * from " + tableName;
    }

    /**
     * Creates a records select query.
     * @param tableName a name of a table
     * @param wheres    where criteria
     * @param orderBy   order by criteria
     * @return          a result query
     */
    public static String querySelect(String tableName, String[][] wheres, String[][] orderBy){
        //"select * from " + TABLE_NAME + " where id="+id+""

        clearStringBuilder();
        sb.append("SELECT * FROM " + tableName);

        buildWhere(wheres);
        buildOrderBy(orderBy);
        return sb.toString();
    }

    /**
     * Creates a records select query.
     * @param tableName a name of a table
     * @param wheres    where criteria
     * @param orderBy   order by criteria
     * @return          a result query
     */
    public static String querySelectOne(String tableName, String[][] wheres, String[][] orderBy){
        String query = DBQueryFactory.querySelect(tableName, wheres, orderBy);
        buildLimit(1);
        return query;
    }

    /**
     * Creates a records select query.
     * @param tableCols table column list
     * @param tables    table list
     * @param wheres    where criteria
     * @param groupBy   group by criteria
     * @param orderBy   order by criteria
     * @param limit     limits record count
     * @return          a result query
     */
    public static String querySelectDetailed(String tableCols, String tables, String[][] wheres, String[] groupBy, String[][] orderBy, int limit) {
        clearStringBuilder();
        sb.append("SELECT ");
        sb.append(tableCols);
        sb.append(" FROM ");
        sb.append(tables);
        buildWhere(wheres);
        buildGroup(groupBy);
        buildOrderBy(orderBy);
        buildLimit(limit);
        return sb.toString();
    }

    /**
     * Builds order by part of a query.
     * @param orderBy   order by criteria
     */
    private static void buildOrderBy(String[][] orderBy){
        if(orderBy == null || orderBy.length <= 0) return;

        sb.append(" ORDER BY ");

        for(int o = 0; o < orderBy.length; o++) {
            sb.append(orderBy[o][0] + " " + orderBy[o][1]);
            if(o < orderBy.length - 1) sb.append(", ");
        }
    }

    /**
     * Builds where part of a query.
     * @param wheres    where criteria
     */
    private static void buildWhere(String[][] wheres){
        if(wheres == null || wheres.length <= 0) return;

        sb.append(" WHERE ");

        for(int w = 0; w < wheres.length; w++) {
            sb.append(wheres[w][0] + " " + wheres[w][1] + " '" + wheres[w][2] + "'");
            if(w < wheres.length - 1) sb.append(" AND ");
        }
    }

    /**
     * Builds group by part of a query.
     * @param groupBy   group by criteria
     */
    private static void buildGroup(String[] groupBy){
        if(groupBy == null || groupBy.length <= 0) return;

        sb.append(" GROUP BY ");

        for(int g = 0; g < groupBy.length; g++) {
            sb.append(groupBy[g]);
            if(g < groupBy.length - 1) sb.append(" AND ");
        }
    }

    /**
     * Builds a limit part of a query.
     * @param limit   limit criteria
     */
    private static void buildLimit(int limit) {
        if(limit > 0)
            sb.append(" LIMIT " + limit);
    }

    /**
     * Builds a record delete query.
     * @param wheres    where criteria
     */
    public static String queryDeleteWhereClause(String[][] wheres){
        int whereCount = wheres.length;
        clearStringBuilder();

        for(int i = 0; i < whereCount; i++) {
            sb.append(wheres[i][0] + " " + wheres[i][1] + " ? ");
            if(i < whereCount - 1) sb.append("AND ");
        }

        return sb.toString();
    }

    /**
     * Transforms where args.
     * @param wheres    where criteria
     * @return          transformed build criteria
     */
    public static String[] queryDeleteWhereArgs(String[][] wheres){
        int whereCount = wheres.length;
        String[] args = new String[whereCount];

        for(int i = 0; i < whereCount; i++) {
            args[i] = String.valueOf(wheres[i][2]);
        }

        return args;
    }

    /**
     * Clears a string builder between building queries.
     */
    private static void clearStringBuilder(){
        sb.setLength(0);
    }

}
