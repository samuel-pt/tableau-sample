/**
 * -----------------------------------------------------------------------------
 *
 * This file is the copyrighted property of Tableau Software and is protected
 * by registered patents and other applicable U.S. and international laws and
 * regulations.
 *
 * Unlicensed use of the contents of this file is prohibited. Please refer to
 * the NOTICES.txt file for further details.
 *
 * -----------------------------------------------------------------------------
 */
package com.tableau.sample.provided;

import com.tableausoftware.TableauException;
import com.tableausoftware.common.*;
import com.tableausoftware.extract.*;
import com.tableausoftware.server.*;

public final class MakeAndPublishOrder {
    /**
     * -------------------------------------------------------------------------
     * Helper Functions
     * -------------------------------------------------------------------------
     */

    // Define the table's schema
    private static TableDefinition makeTableDefinition() throws TableauException {
        TableDefinition tableDef = new TableDefinition();
        tableDef.setDefaultCollation(Collation.EN_GB);

        tableDef.addColumn("Purchased",       Type.DATETIME);
        tableDef.addColumn("Product",         Type.CHAR_STRING);
        tableDef.addColumn("uProduct",        Type.UNICODE_STRING);
        tableDef.addColumn("Price",           Type.DOUBLE);
        tableDef.addColumn("Quantity",        Type.INTEGER);
        tableDef.addColumn("Taxed",           Type.BOOLEAN);
        tableDef.addColumn("Expiration Date", Type.DATE);

        // Column with non-default collation
        tableDef.addColumnWithCollation("Produkt", Type.CHAR_STRING, Collation.DE);

        return tableDef;
    }

    // Print a Table's schema to stderr.
    private static void printTableDefinition(TableDefinition tableDef) throws TableauException {
        int numColumns = tableDef.getColumnCount();
        for ( int i = 0; i < numColumns; ++i ) {
            Type type = tableDef.getColumnType(i);
            String name = tableDef.getColumnName(i);

            System.err.format("Column %d: %s (%#06x)\n", i, name, type.getValue());
        }
    }

    // Insert a few rows of data
    private static void insertData(Table table) throws TableauException {
        TableDefinition tableDef = table.getTableDefinition();
        Row row = new Row(tableDef);

        row.setDateTime(  0, 2012, 7, 3, 11, 40, 12, 4550); // Purchased
        row.setCharString(1, "Beans");                      // Product
        row.setString(    2, "uniBeans");                   // uProduct
        row.setDouble(    3, 1.08);                         // Price
        row.setDate(      6, 2029, 1, 1);                   // Expiration date
        row.setCharString(7, "Bohnen");                     // Produkt

        for ( int i = 0; i < 10; ++i ) {
            row.setInteger(4, i * 10);                      // Quantity
            row.setBoolean(5, i % 2 == 1);                  // Taxed
            table.insert(row);
        }
    }

     /**
     * -------------------------------------------------------------------------
     * Main
     * -------------------------------------------------------------------------
     */
    public static void main( String[] args ) {

        // Create Extract
        try {
            // Initialize Tableau Extract API
            ExtractAPI.initialize();

            try (Extract extract = new Extract("order2-java.tde")) {

                Table table;
                if (!extract.hasTable("Extract")) {
                    // Table does not exist; create it
                    TableDefinition tableDef = makeTableDefinition();
                    table = extract.addTable("Extract", tableDef);
                }
                else {
                    // Open an existing table to add more rows
                    table = extract.openTable("Extract");
                }

                TableDefinition tableDef = table.getTableDefinition();
                printTableDefinition(tableDef);

                insertData(table);
            }

            // Clean up Tableau Extract API
            ExtractAPI.cleanup();
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
        }

        // Publish Extract
        try {
            // Initialize Tableau Server API
            ServerAPI.initialize();

            // Create the server connection object
            ServerConnection serverConnection = new ServerConnection();

            // Connect to the server
            serverConnection.connect("http://localhost", "username", "password", "siteID");

            // Publish order-java.tde to the server under the default project with name Order-java
            serverConnection.publishExtract("order2-java.tde", "default", "Order2-java", false);

            // Disconnect from the server
            serverConnection.disconnect();

            // Destroy the server connection object
            serverConnection.close();

            // Clean up Tableau Server API
            ServerAPI.cleanup();
        }
        catch (TableauException e) {
            // Handle the exception depending on the type of exception received

            switch(Result.enumForValue(e.getErrorCode())) {
            case INTERNAL_ERROR:
                System.err.println("INTERNAL_ERROR - Could not parse the response from the server.");
                break;
            case INVALID_ARGUMENT:
                System.err.println("INVALID_ARGUMENT - " + e.getMessage());
                break;
            case CURL_ERROR:
                System.err.println("CURL_ERROR - " + e.getMessage());
                break;
            case SERVER_ERROR:
                System.err.println("SERVER_ERROR - " + e.getMessage());
                break;
            case NOT_AUTHENTICATED:
                System.err.println("NOT_AUTHENTICATED - " + e.getMessage());
                break;
            case BAD_PAYLOAD:
                System.err.println("BAD_PAYLOAD - Unknown response from the server. Make sure this version of Tableau API is compatible with your server.");
                break;
            case INIT_ERROR:
                System.err.println("INIT_ERROR - " + e.getMessage());
                break;
            case UNKNOWN_ERROR:
            default:
                System.err.println("An unknown error occured.");
                break;
            }
            e.printStackTrace(System.err);
        }
    }
}
