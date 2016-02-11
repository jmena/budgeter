package me.bgx.budget.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldDescription {
    String label;
    String path;
    String type;
    int order;
}
