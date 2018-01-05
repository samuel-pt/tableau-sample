package com.tableau.sample;

import com.tableausoftware.TableauException;
import com.tableausoftware.server.ServerAPI;
import com.tableausoftware.server.ServerConnection;

public final class TDEPublisher {

  public void publish(String serverUrl, String username, String password,
                      String site, String tde, String project, String datasource) {

    try {
      // Initialize Tableau Server API
      ServerAPI.initialize();

      // Create the server connection object
      ServerConnection serverConnection = new ServerConnection();

      // Connect to the server
      serverConnection.connect(serverUrl, username, password, site);

      // Publish order-java.tde to the server under the default project with name Order-java
      serverConnection.publishExtract(tde, project, datasource, false);

      // Disconnect from the server
      serverConnection.disconnect();

      // Destroy the server connection object
      serverConnection.close();

      // Clean up Tableau Server API
      ServerAPI.cleanup();
    } catch (TableauException te) {
      te.printStackTrace();
    }
  }
}