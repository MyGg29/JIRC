package models;

@FunctionalInterface
public interface Function<One, Two, Three, Four> {
    public Four apply(One channel, Two sender, Three message);
}
