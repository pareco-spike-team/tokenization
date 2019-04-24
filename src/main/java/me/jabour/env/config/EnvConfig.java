package me.jabour.env.config;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvConfig {
  private final static Logger logger =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected static final String NAMESPACE_PATTERN = "[_\\.]";
  protected String target = null;
  protected String prefix = null;
  protected String namespace = null;

  public EnvConfig(String prefix, String namespace, String target) {
    this.prefix = Optional.ofNullable(prefix).orElse("");

    this.namespace = Optional.ofNullable(namespace).orElse("");

    this.target = Optional.ofNullable(target)
        .orElse(Optional.ofNullable(fetch("JAVA_ENV")).orElse("development"));
  }

  public EnvConfig() {
    this.prefix = "";
    this.namespace = "";
    this.target = Optional.ofNullable(fetch("JAVA_ENV")).orElse("development");
  }

  protected List<String> getPrefixes() {
    return ImmutableList.<String>copyOf(prefix.split(NAMESPACE_PATTERN));
  }

  protected List<String> getNamespaces() {
    return ImmutableList.<String>copyOf(namespace.split(NAMESPACE_PATTERN));
  }

  protected List<String> getTargets() {
    return ImmutableList.<String>copyOf(target.split(NAMESPACE_PATTERN));
  }

  protected List<String> keysFor(String key) {
    return ImmutableList
        .<String>copyOf(Optional.<String>ofNullable(key).orElse("").split(NAMESPACE_PATTERN));
  }

  protected String withPrefixesNamespacesTargetsKeyFor(List<String> keys) {
    return String.join("_", ImmutableList.<String>builder().addAll(getPrefixes())
        .addAll(getNamespaces()).addAll(getTargets()).addAll(keys).build()).toUpperCase();
  }

  protected String withNamespacesTargetsKeyFor(List<String> keys) {
    return String.join("_", ImmutableList.<String>builder().addAll(getNamespaces())
        .addAll(getTargets()).addAll(keys).build()).toUpperCase();
  }

  protected String withPrefixesNamespacesKeyFor(List<String> keys) {
    return String.join("_", ImmutableList.<String>builder().addAll(getPrefixes())
        .addAll(getNamespaces()).addAll(keys).build()).toUpperCase();
  }

  protected String withNamespacesKeyFor(List<String> keys) {
    return String
        .join("_", ImmutableList.<String>builder().addAll(getNamespaces()).addAll(keys).build())
        .toUpperCase();
  }

  protected String withPrefixesKeyFor(List<String> keys) {
    return String.join("_", ImmutableList.<String>builder().addAll(getPrefixes())
        .addAll(getNamespaces()).addAll(keys).build()).toUpperCase();
  }

  protected String withKeyFor(List<String> keys) {
    return String.join("_", ImmutableList.<String>builder().addAll(keys).build()).toUpperCase();
  }

  protected List<String> fetchKeysFor(List<String> keys) {
    List<String> fetchKeys = ImmutableList.<String>builder()
        .add(withPrefixesNamespacesTargetsKeyFor(keys)).add(withNamespacesTargetsKeyFor(keys))
        .add(withPrefixesNamespacesKeyFor(keys)).add(withNamespacesKeyFor(keys))
        .add(withPrefixesKeyFor(keys)).add(withKeyFor(keys)).build().stream()
        .filter(Predicates.containsPattern("^[^_].+$")).collect(Collectors.toList());

    logger.debug("fetchKeysFor({}) -> {}", keys, fetchKeys);

    return fetchKeys;
  }

  public String fetch(String name) {
    if (name.isEmpty()) {
      logger.debug("fetch({}) -> {}, {}", name, null, "name is empty");

      return null;
    }

    try {
      String value = System.getenv(name);

      logger.debug("fetch({}) -> {}", name, value);

      return value;
    } catch (NullPointerException | SecurityException exception) {
      logger.debug("fetch({}) -> {}, {}", name, null, exception);

      return null;
    }
  }

  public Optional<String> optional(String name) {
    return Optional.ofNullable(fetch(fetchKeysFor(keysFor(name)).stream()
        .filter(Predicates.in(System.getenv().keySet())).findFirst().orElse("")));
  }

  public String required(String name) throws NoSuchElementException {
    return optional(name).get();
  }
}
