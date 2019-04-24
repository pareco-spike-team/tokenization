package org.pareco.tokenization.models;

import lombok.Getter;
import lombok.Setter;

public class Token {
  @Getter
  @Setter
  private String type;

  @Getter
  @Setter
  private String value;
}
