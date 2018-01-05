package com.tableau.sample;

import com.tableausoftware.TableauException;
import com.tableausoftware.common.Collation;
import com.tableausoftware.common.Type;
import com.tableausoftware.extract.*;

import java.util.Calendar;

/**
 * Sample to create TDE
 */
public class TDECreator {

  // Define the table's schema
  private TableDefinition createTableDefinition() throws TableauException {

    TableDefinition tableDef = new TableDefinition();
    tableDef.setDefaultCollation(Collation.EN_US);

    tableDef.addColumn("Product",         Type.CHAR_STRING);
    tableDef.addColumn("Quantity",        Type.INTEGER);
    tableDef.addColumn("Purchased",       Type.DATETIME);
    tableDef.addColumn("Price",           Type.DOUBLE);
    tableDef.addColumn("Taxed",           Type.BOOLEAN);
    tableDef.addColumn("Expiration Date", Type.DATE);

    return tableDef;
  }

  private void insertSampleData(Table table, long rowCount) throws TableauException {
    TableDefinition tableDef = table.getTableDefinition();
    Row row = new Row(tableDef);

    for ( int i = 1; i <= rowCount; ++i ) {
      row.setCharString(0, "Product_" + i);
      row.setInteger(1, i + 1);
      Calendar cal = Calendar.getInstance();
      row.setDateTime(2, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
              cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
              cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND),
              cal.get(Calendar.MILLISECOND));
      row.setDouble(3, 10 * i);
      row.setBoolean(4, false);
      row.setDate(5, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
              cal.get(Calendar.DAY_OF_MONTH));

      table.insert(row);
    }
  }

  public String createSampleExtract(String datasource, long rowCount) {
    String tde = datasource + ".tde";
    try {
      // Initialize Tableau Extract API
      ExtractAPI.initialize();

      try (Extract extract = new Extract(tde)) {

        Table table;
        if (!extract.hasTable("Extract")) {
          // Table does not exist; create it
          TableDefinition tableDef = createTableDefinition();
          table = extract.addTable("Extract", tableDef);
        } else {
          // Open an existing table to add more rows
          table = extract.openTable("Extract");
        }

        insertSampleData(table, rowCount);
      }

      // Clean up Tableau Extract API
      ExtractAPI.cleanup();
      System.out.println("Extract " + tde + " created");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return tde;
  }

}
