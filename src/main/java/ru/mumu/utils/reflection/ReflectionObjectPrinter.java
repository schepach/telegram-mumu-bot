package ru.mumu.utils.reflection;

import org.apache.log4j.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by alexey on 10.08.16.
 */
public class ReflectionObjectPrinter {

    private static final Logger LOGGER = Logger.getLogger(ReflectionObjectPrinter.class.getSimpleName());

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static String getCalendarString(Calendar date) {
        return df.format(date.getTime());
    }

    public static String convertJAXBElementToString(JAXBElement value) {
        return String.valueOf(value.getValue());
    }

    public static String getXMLGregorianCalendarString(XMLGregorianCalendar date) {
        return getDateString(date.toGregorianCalendar().getTime());
    }

    public static String getDateString(Date date) {
        return df.format(date.getTime());
    }

    private static String listToString(Object list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Object o : (List) list) {
            if (o.getClass().getPackage().getName().startsWith("java")) {
                sb.append(o);
            } else {
                sb.append("[");
                sb.append(toString(o, o.getClass()));
                sb.append("]");
            }
            sb.append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    public static String toString(Object o) {
        if (o != null) {
            if (o instanceof List) {
                return listToString(o);
            }
            if (o.getClass().getPackage().getName().startsWith("java")) {
                return o.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getSimpleName()).append(":");
            sb.append(toString(o, o.getClass()));

            return sb.toString();
        }
        return null;
    }

    private static String toString(Object o, Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(o);
                    sb.append("[");
                    sb.append(f.getName());
                    sb.append("]");
                    sb.append("=");
                    if (value instanceof XMLGregorianCalendar) {
                        sb.append(getXMLGregorianCalendarString((XMLGregorianCalendar) value));
                    } else if (value instanceof Calendar) {
                        sb.append(getCalendarString((Calendar) value));
                    } else if (value instanceof Date) {
                        sb.append(getDateString((Date) value));
                    } else if (value instanceof List) {
                        sb.append(listToString(value));
                    } else if (value instanceof JAXBElement) {
                        sb.append(convertJAXBElementToString((JAXBElement) value));
                    } else {
                        sb.append(value != null
                                ? (value.getClass().getPackage().getName().startsWith("java")
                                ? value
                                : toString(value))
                                : "null");
                    }
                    sb.append(";");
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    LOGGER.error(ex.getMessage());
                }
            }
        }
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            sb.append(toString(o, clazz.getSuperclass()));
        }
        return sb.toString();
    }
}
