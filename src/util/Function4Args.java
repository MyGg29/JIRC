package util;

@FunctionalInterface
public interface Function4Args<One, Two, Three, Four> {
    public Four apply(One channel, Two sender, Three message);
}
