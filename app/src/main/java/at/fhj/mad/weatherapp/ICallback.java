package at.fhj.mad.weatherapp;

/**
 * Created by krajn on 16/10/15.
 */
public interface ICallback {

    /**
     * this method should pass a jsons tring to a implementation
     * @param jsonString
     */
    public void handleJSonString(String jsonString);

}
