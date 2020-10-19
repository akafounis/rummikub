package communication;

import com.google.gson.*;
import communication.request.Request;

import java.lang.reflect.Type;

/**
 * Class specifying custom serialization for interfaces.
 * Needed to be able to distinguish between different implementations of the interface.
 *
 * @param <T> type of the Interface
 */
final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

  public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
    final JsonObject wrapper = new JsonObject();
    wrapper.addProperty("type", object.getClass().getName());
    wrapper.add("data", context.serialize(object));
    return wrapper;
  }

  public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
    final JsonObject wrapper = (JsonObject) elem;
    final JsonElement typeName = get(wrapper, "type");
    final JsonElement data = get(wrapper, "data");
    final Type actualType = typeForName(typeName);
    return context.deserialize(data, actualType);
  }

  private Type typeForName(final JsonElement typeElem) {
    try {
      return Class.forName(typeElem.getAsString());
    } catch (ClassNotFoundException e) {
      throw new JsonParseException(e);
    }
  }

  private JsonElement get(final JsonObject wrapper, String memberName) {
    final JsonElement elem = wrapper.get(memberName);
    if (elem == null)
      throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
    return elem;
  }
}