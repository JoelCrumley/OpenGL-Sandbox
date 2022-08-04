package joel.opengl.util;

import joel.opengl.maths.Maths;

import java.io.PrintStream;

public class TimerUtil {

    public enum Type {

        GENERIC, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

    }

    public static final int TIMER_COUNT = Type.values().length;
    private static long[] start = new long[TIMER_COUNT];

    public static long getTime(int id) {
        return System.currentTimeMillis() - getStartTime(id);
    }

    public static long getTime(Type type) {
        return getTime(type.ordinal());
    }

    public static long getTime() {
        return getTime(0);
    }

    public static long getStartTime(int id) {
        return start[Maths.clamp(id, 0, TIMER_COUNT-1)];
    }

    public static long getStartTime(Type type) {
        return getStartTime(type.ordinal());
    }

    public static long getStartTime() {
        return getStartTime(0);
    }

    public static void start(int id) {
        id = Maths.clamp(id, 0, TIMER_COUNT-1);
        start[id] = System.currentTimeMillis();
    }
    public static void start(Type type) {
        start(type.ordinal());
    }
    public static void start() {
        start(0);
    }

    public static void start(int id, String message, PrintStream stream) {
        stream.println(message);
        start(id);
    }

    public static void start(Type type, String message, PrintStream stream) {
        stream.println(message);
        start(type);
    }

    public static void start(String message, PrintStream stream) {
        start(0, message, stream);
    }
    public static void start(int id, String message) {
        start(id, message, System.out);
    }
    public static void start(Type type, String message) {
        start(type, message, System.out);
    }
    public static void start(String message) {
        start(0, message, System.out);
    }

    public static void end(int id, String message, PrintStream stream) {
        id = Maths.clamp(id, 0, TIMER_COUNT-1);
        long end = System.currentTimeMillis();
        stream.println(message + " (" + (end-start[id]) + "ms)");
        start[id] = end;
    }

    public static void end(Type type, String message, PrintStream stream) {
        end(type.ordinal(), message, stream);
    }

    public static void end(String message, PrintStream stream) {
        end(0, message, stream);
    }
    public static void end(int id, String message) {
        end(id, message, System.out);
    }
    public static void end(Type type, String message) {
        end(type, message, System.out);
    }
    public static void end(String message) {
        end(0, message);
    }

}