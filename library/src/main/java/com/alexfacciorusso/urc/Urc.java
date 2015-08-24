package com.alexfacciorusso.urc;


import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Uniform Resource Creator. A class that helps user to create URLs for RESTful apis.<br />
 * </p>
 *
 * @author Alex Facciorusso
 */
@SuppressWarnings("unused")
public class Urc {
    private String mBaseUrl;

    private Urc() {
    }

    private Urc(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public static Urc with(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return new Urc(baseUrl);
    }

    /**
     * <p>
     * The endpoint syntax is taken from Slim framework, so it is in the form of {@code
     * '/static/:argument' } .
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
     * @param endpoint The path in :argument form
     * @return An {@code UrcGenerator object}
     * @see com.alexfacciorusso.urc.Urc.UrcGenerator
     */
    public UrcGenerator fromEndpoint(String endpoint) {
        return new UrcGenerator(endpoint);
    }

    /**
     * This class is a builder that generates final paths with abstract ones.
     */
    public class UrcGenerator {
        private final Pattern ABSTRACT_PATTERN = Pattern.compile(":([\\w]+)");

        private String mEndpoint;
        private Map<String, String> mParameters = new HashMap<>();
        private Map<String, String> mQueryParameters = new HashMap<>();
        private Map<String, String> mPathParameters = new HashMap<>();
        private boolean mIgnoreUnmatchedParameters = false;

        public UrcGenerator(String endpoint) {
            mEndpoint = endpoint;
        }

        /**
         * Add a query parameter to Uri. Parameters given by this method will overwrite eventual
         * not matched parameters given by {@link #addParameter(String, String)}
         * if {@link #ignoreUnmatchedParameters()} is not used.
         *
         * @param key   the parameter's key
         * @param value the parameter's content
         * @return the current instance of {@link com.alexfacciorusso.urc.Urc.UrcGenerator}
         * @see #addParameter(String, String)
         */
        public UrcGenerator addQueryParameter(String key, String value) {
            mQueryParameters.put(key, Uri.encode(value));

            return this;
        }


        /**
         * @see #addQueryParameter(String, String)
         */
        public UrcGenerator addQueryParameter(String key, int value) {
            mQueryParameters.put(key, String.valueOf(value));
            return this;
        }


        /**
         * @see #addQueryParameter(String, String)
         */
        public UrcGenerator addQueryParameter(String key, Object value) {
            mQueryParameters.put(key, value.toString());
            return this;
        }

        /**
         * Add a parameter to Uri. If {@code key} is found as :parameter in the given path, then
         * will be replaced to that parameter. Else it will be silently ignored.
         *
         * @param key   the parameter's key
         * @param value the parameter's content
         * @return the current instance of {@link com.alexfacciorusso.urc.Urc.UrcGenerator}
         * @see #addParameter(String, String)
         */
        public UrcGenerator addPathParameter(String key, String value) {
            mPathParameters.put(key, Uri.encode(value));
            return this;
        }

        /**
         * @see #addPathParameter(String, String)
         */
        public UrcGenerator addPathParameter(String key, int value) {
            mPathParameters.put(key, String.valueOf(value));
            return this;
        }

        /**
         * @see #addPathParameter(String, String)
         */
        public UrcGenerator addPathParameter(String key, Object value) {
            mPathParameters.put(key, value.toString());
            return this;
        }

        /**
         * Add a parameter to Uri. If {@code key} is found as :parameter in the given path, then
         * will be replaced to that parameter. Else:
         * <ul>
         * <li>Default behavior: the parameter will be added as query parameter (e.g.
         * ?key=value)</li>
         * <li>If {@link #ignoreUnmatchedParameters()} used, the unmatched parameter will be
         * ignored.</li></ul>
         *
         * @param key   the parameter's key
         * @param value the parameter's content
         * @return the current instance of {@link com.alexfacciorusso.urc.Urc.UrcGenerator}
         */
        public UrcGenerator addParameter(String key, String value) {
            mParameters.put(key, Uri.encode(value));
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
         * @deprecated You <b>MUST</b> use the {@link #ignoreUnmatchedParameters()} method.
         */
        @Deprecated
        public UrcGenerator setQueryParametersEnabled(boolean enabled) {
            mIgnoreUnmatchedParameters = !enabled;
            return this;
        }

        /**
         * Disable the automatic query parameters generation for unmatched parameters added with
         * {@link #addParameter(String, String)}.
         *
         * <b>Note: </b>This method doesn't change the behavior of parameters added with
         * {@link #addPathParameter(String, String)} or {@link #addQueryParameter(String, String)}.
         */
        public UrcGenerator ignoreUnmatchedParameters() {
            mIgnoreUnmatchedParameters = true;
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
                if (mPathParameters.containsKey(key)) {
                    m.appendReplacement(sb, Matcher.quoteReplacement(mPathParameters.get(key)));
                } else {
                    if (mParameters.containsKey(key)) {
                        m.appendReplacement(sb, Matcher.quoteReplacement(mParameters.get(key)));
                    } else {
                        m.appendReplacement(sb, key);
                    }
                }

                if (mParameters.containsKey(key)) {
                    mParameters.remove(key);
                }
            }

            m.appendTail(sb);

            // Query parameters has higher priority than unmatched auto parameters.
            for (String key : mQueryParameters.keySet()) {
                if (mParameters.containsKey(key)) {
                    mParameters.remove(key);
                }
            }

            String finalPath = sb.toString();
            if (!finalPath.startsWith("/")) {
                finalPath = "/" + finalPath;
            }

            Uri uri = Uri.parse(mBaseUrl + finalPath);
            Uri.Builder builder = uri.buildUpon();

            for (Map.Entry<String, String> entry : mQueryParameters.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
                if (mParameters.containsKey(entry.getKey())) {
                    mParameters.remove(entry.getKey());
                }
            }

            if (!mIgnoreUnmatchedParameters) {
                for (Map.Entry<String, String> entry : mParameters.entrySet()) {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }
            }

            return builder.build().toString();
        }
    }
}
