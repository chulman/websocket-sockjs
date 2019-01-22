package com.chulm.websocket.netty.server.frame;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.charset.Charset;


/**
 * ref link
 * //https://github.com/cgbystrom/sockjs-netty
 */

public abstract class Frame {

    public static byte[] OPEN_FRAME = "o".getBytes(Charset.defaultCharset());
    public static byte[] OPEN_FRAME_NL = "o\n".getBytes(Charset.defaultCharset());
    public static byte[] HEARTBEAT_FRAME = "h".getBytes(Charset.defaultCharset());
    public static byte[] HEARTBEAT_FRAME_NL = "h\n".getBytes(Charset.defaultCharset());


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected ByteBuf data;

    public ByteBuf getData() {
        return data;
    }

    public static CloseFrame closeFrame(int status, String reason) {
        return new CloseFrame(status, reason);
    }

    public static MessageFrame messageFrame(SockJsMessage... messages) {
        return new MessageFrame(messages);
    }

    public static SockJsMessage messageParse(String jsonData) {

        try {
            return OBJECT_MAPPER.readValue(jsonData, SockJsMessage.class);
        } catch (JsonProcessingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private static ByteBuf generatePreludeFrame(char c, int num, boolean appendNewline) {
        ByteBuf cb = Unpooled.buffer(num + 1);
        for (int i = 0; i < num; i++) {
            cb.writeByte(c);
        }
        if (appendNewline)
            cb.writeByte('\n');
        return cb;
    }

    public static String escapeCharacters(char[] value) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            char ch = value[i];
            if ((ch >= '\u0000' && ch <= '\u001F') ||
                    (ch >= '\uD800' && ch <= '\uDFFF') ||
                    (ch >= '\u200C' && ch <= '\u200F') ||
                    (ch >= '\u2028' && ch <= '\u202F') ||
                    (ch >= '\u2060' && ch <= '\u206F') ||
                    (ch >= '\uFFF0' && ch <= '\uFFFF')) {
                String ss = Integer.toHexString(ch);
                buffer.append('\\');
                buffer.append('u');
                for (int k = 0; k < 4 - ss.length(); k++) {
                    buffer.append('0');
                }
                buffer.append(ss.toLowerCase());
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }


    public static void escapeJson(ByteBuf input, ByteBuf buffer) {
        for (int i = 0; i < input.readableBytes(); i++) {
            byte ch = input.getByte(i);
            switch (ch) {
                case '"':
                    buffer.writeByte('\\');
                    buffer.writeByte('\"');
                    break;
                case '/':
                    buffer.writeByte('\\');
                    buffer.writeByte('/');
                    break;
                case '\\':
                    buffer.writeByte('\\');
                    buffer.writeByte('\\');
                    break;
                case '\b':
                    buffer.writeByte('\\');
                    buffer.writeByte('b');
                    break;
                case '\f':
                    buffer.writeByte('\\');
                    buffer.writeByte('f');
                    break;
                case '\n':
                    buffer.writeByte('\\');
                    buffer.writeByte('n');
                    break;
                case '\r':
                    buffer.writeByte('\\');
                    buffer.writeByte('r');
                    break;
                case '\t':
                    buffer.writeByte('\\');
                    buffer.writeByte('t');
                    break;

                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') ||
                            (ch >= '\uD800' && ch <= '\uDFFF') ||
                            (ch >= '\u200C' && ch <= '\u200F') ||
                            (ch >= '\u2028' && ch <= '\u202F') ||
                            (ch >= '\u2060' && ch <= '\u206F') ||
                            (ch >= '\uFFF0' && ch <= '\uFFFF')) {
                        String ss = Integer.toHexString(ch);
                        buffer.writeByte('\\');
                        buffer.writeByte('u');
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            buffer.writeByte('0');
                        }
                        buffer.writeBytes(ss.toLowerCase().getBytes());
                    } else {
                        buffer.writeByte(ch);
                    }
            }
        }
    }

    public static class CloseFrame extends Frame {
        private int status;
        private String reason;

        private CloseFrame(int status, String reason) {
            this.status = status;
            this.reason = reason;
            // FIXME: Must escape status and reason
            data = Unpooled.copiedBuffer("c[" + status + ",\"" + reason + "\"]", CharsetUtil.UTF_8);
        }

        public int getStatus() {
            return status;
        }

        public String getReason() {
            return reason;
        }
    }

    public static class MessageFrame extends Frame {
        private SockJsMessage[] messages;

        private MessageFrame(SockJsMessage... messages) {
            this.messages = messages;
            data = Unpooled.directBuffer();
            data.writeByte('a');
            data.writeByte('[');
            for (int i = 0; i < messages.length; i++) {
                SockJsMessage message = messages[i];
                data.writeByte('"');
                char[] escaped = new JsonStringEncoder().quoteAsString(message.getMessage());
                data.writeBytes(Unpooled.copiedBuffer(escapeCharacters(escaped), CharsetUtil.UTF_8));
                data.writeByte('"');
                if (i < messages.length - 1) {
                    data.writeByte(',');
                }
            }

            data.writeByte(']');
        }

        public SockJsMessage[] getMessages() {
            return messages;
        }
    }
}