package com.balsamic.sejongmalsami_monitoring.util.log;

import com.balsamic.sejongmalsami_monitoring.object.constants.Author;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiChangeLog {

  String date();

  Author author();

  String description();
}
