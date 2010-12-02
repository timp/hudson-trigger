package net.pizey.hudson.trigger;


/**
 * @author timp
 *
 */
public class TriggerTestCase extends JettyWebTestCase {

  public TriggerTestCase() {
  }

  public TriggerTestCase(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  /**
   * If you don't know by now.
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    actualPort = startServer(8080);
  }

  public void testGet() {
    beginAt("/Trigger");
    assertTextPresent("Trigger");
  }
  
  public void testPost() { 
    beginAt("/Trigger?token=rof_G6T8S_bWbAGM");
    assertTextPresent("Trigger");
    submit();
    assertTextPresent("Triggered");
    assertTextPresent("200");
  }
  public void testPostUnknown() { 
    beginAt("/Trigger?token=unknown");
    assertTextPresent("Trigger");
    submit();
    assertTextPresent("Triggered");
    assertTextPresent("0");
    assertTextPresent("No build targets found");
  }
}
