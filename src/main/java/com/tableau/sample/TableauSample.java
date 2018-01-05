package com.tableau.sample;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public final class TableauSample {
  @Parameter(names = "-serverUrl", description = "Tableau Server URL")
  private String serverUrl;

  @Parameter(names = "-username", description = "Tableau Username")
  private String username;

  @Parameter(names = "-password", description = "Tableau Password")
  private String password;

  @Parameter(names = "-site", description = "Tableau Site")
  private String site;

  @Parameter(names = "-datasource", description = "Tableau DatSource")
  private String datasource;

  @Parameter(names = "-doNotPublish", description = "Whether to publish datasource or not")
  private boolean doNotPublish = false;

  @Parameter(names = "-noOfRows", description = "Number of rows to be added")
  private long noOfRows = 10;

  @Parameter(names = "-project", description = "Tableau Project")
  private String project;

  public void run() {
    String tde = new TDECreator().createSampleExtract(datasource, noOfRows);
    System.out.println("TDE " + tde + " Created successfully");
    if (!doNotPublish) {
      new TDEPublisher().publish(serverUrl, username, password, site, tde, project, datasource);
      System.out.println("TDE Published successfully");
    }
  }

  private void validate() {
    validate(serverUrl, "-serverUrl");
    validate(username, "-username");
    validate(password, "-password");
    validate(site, "-site");
    validate(datasource, "-datasource");
  }

  private void validate(String param, String key) {
    if (param == null || param.isEmpty()) {
      throw new ParameterException("Value for parameter '" + key + "' not set, Please make sure to pass all arguments \n" +
              "Example : java -jar tableau-sample-1.0.0.jar -serverUrl http://online.tableau.com " +
              "-username <Tableau Username> -password <Tableau Password>, -site <Tableau Site> -datasource <DataSource name>" );
    }
  }

  public static void main(String args[]) {
    TableauSample sample = new TableauSample();
    new JCommander(sample, args);
//    sample.validate();
    sample.run();
  }

}