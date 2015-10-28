package at.fhj.mad.weatherapp;

/**
 * Callback for Listening to the asynctask
 *

 +-----------------------------+
 |                             | <-----------------+
 |        ICallback            |                   |
 |                             |        +----------+---------+
 +------------+----------------+        |                    |
              ^                         |   Helper           |
              | Interface               |                    |
 |            |                         |                    |
 +------------+---------------+         +-----------+--------+
 |           Activity         |                     ^
 |                            |                     |
 |                            +---------------------+
 +----------------------------+



 */
public interface ICallback {

    /**
     * this method should pass a jsons tring to a implementation
     * @param jsonString
     */
    public void handleJSonString(String jsonString);

}
