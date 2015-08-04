package com.alexfacciorusso.urc;


import android.net.Uri;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Uniform Resource Creator. A class that helps user to create URLs for RESTful apis.<br />
 * </p>
 *
 * @author Alex Facciorusso
 */
public class Urc {
    private static final String TAG = "Urc";

    private String mBaseUrl;

    private Urc() {
    }

    public static Urc with(String baseUrl) {
        Urc urc = new Urc();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length()-1);
        }
        urc.setBaseUrl(baseUrl);
        return urc;
    }

    /**
     * <p>
     *     The path syntax is taken from Slim framework, so it is in the form of {@code '/static/:argument' } .
     * </p>
     *
     * <b>Example:</b>
     * <pre>
     *     Urc myApi = Urc.with("https://my-api.com/v1");
     *     myApi.fromEndpoint("/:firstArgument/try/:second")
     *          .addParameter("firstArgument", "first")
     *          .addParameter("second", 2)
     *          .addParameter("noListed", "hello")
     *          .build();
     * </pre>
     *
     * The result url will be: {@code https://google.com/1/try/2 }
     *
     * @param path The path in :argument form
     * @return An {@code UrcGenerator object}
     *
     * @see com.alexfacciorusso.urc.Urc.UrcGenerator
     */
    public UrcGenerator fromEndpoint(String path) {
        return new UrcGenerator(path);
    }


    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * This class is a builder that generates final paths with abstract ones.
     */
    public class UrcGenerator {
        private final Pattern ABSTRACT_PATTERN = Pattern.compile(":([\\w]+)");

        private String mEndpoint;
        private Map<String, String> mParameters = new HashMap<>();
        private Set<String> mUsedKeys = new HashSet<>();
        private boolean mQueryParametersEnabled = true;

        public UrcGenerator(String endpoint) {
            mEndpoint = endpoint;
        }

        /**
         * Add a parameter to Uri. If {@code key} is found as :parameter in the given path, then will
         * be replaced to that parameter. Else:
         * <ul>
         *     <li>Default behavior: the parameter will be added as query parameter (e.g. ?key=value)</li>
         *     <li>If {@link #setQueryParametersEnabled(boolean)} set to {@code false},
         *     the unmatched parameter will be ignored.</li>
         * </ul>
         * @param key the parameter's key
         * @param value the parameter's content
         * @return the current instance of {@link com.alexfacciorusso.urc.Urc.UrcGenerator}
         */
        public UrcGenerator addParameter(String key, String value) {
            mParameters.put(key, value);
            return this;
        }

        /**
         * @see #addParameter(String, String)
         */
        public UrcGenerator addParameter(String key, int value) {
            return addParameter(key, String.valueOf(value));
        }

        /**
         * @see #addParameter(String, String)
         */
        public UrcGenerator addParameter(String key, Object value) {
            return addParameter(key, value.toString());
        }

        /**
         * Enable or disable the query parameters for unmatched parameters.
         *
         * @see #addParameter(String, String)
         */
        public UrcGenerator setQueryParametersEnabled(boolean enabled) {
            mQueryParametersEnabled = enabled;
            return this;
        }

        /**
         * Builds the url and returns it.
         *
         * @return the final uri/url
         */
        public String build() {
            Matcher m = ABSTRACT_PATTERN.matcher(mEndpoint);
            StringBuffer sb = new StringBuffer(mEndpoint.length());
            while (m.find()) {
                String key = m.group(1);
                if (mParameters.containsKey(key)) {
                    mUsedKeys.add(key);
                    m.appendReplacement(sb, Matcher.quoteReplacement(mParameters.get(key)));
                } else {
                    m.appendReplacement(sb, key);
                }
            }
            for (String key : mUsedKeys) {
                mParameters.remove(key);
            }

            m.appendTail(sb);

            String finalPath = sb.toString();
            if (!finalPath.startsWith("/")) {
                finalPath = "/" + finalPath;
            }

            Uri uri = Uri.parse(mBaseUrl + finalPath);
            Uri.Builder builder = uri.buildUpon();

            if (mQueryParametersEnabled) {
                for (Map.Entry<String, String> entry : mParameters.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
            }

            return builder.build().toString();
        }
    }
}
