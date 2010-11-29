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

  public void testGet() {
    beginAt("/Trigger");
    assertTextPresent("Trigger");
  }
  
  public void testPost() { 
    beginAt("/Trigger?token=DO5TiHT2E7yLueGN");
    assertTextPresent("Trigger");
    submit();
    assertTextPresent("Triggered");
    assertTextPresent("200");
  }
}