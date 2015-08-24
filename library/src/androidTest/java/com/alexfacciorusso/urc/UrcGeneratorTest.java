package com.alexfacciorusso.urc;

import junit.framework.TestCase;

/**
 * @author alexfacciorusso
 * @since 12/08/15.
 */
public class UrcGeneratorTest extends TestCase {
    public static final String BASE_URL = "http://test.com/";
    private Urc mService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mService = Urc.with(BASE_URL);
    }

    public void testBuild() throws Exception {
        Urc.UrcGenerator e1 = mService.fromEndpoint("/hi/");
        assertEquals(BASE_URL + "hi/", e1.build());

        Urc.UrcGenerator service = mService.fromEndpoint("/test/:id")
                .addParameter("id", "1")
                .addPathParameter("id", 2);

        assertEquals(BASE_URL + "test/2", service.build());

        service = mService.fromEndpoint("/test/:id")
                .addPathParameter("id", 2)
                .addParameter("id", "1");

        assertEquals(BASE_URL + "test/2", service.build());

        service = mService.fromEndpoint("/test/:id")
                .addPathParameter("id", 2)
                .addParameter("id", "1");

        assertEquals(BASE_URL + "test/2", service.build());

        service = mService.fromEndpoint("/test/:id")
                .addParameter("id", 1)
                .addParameter("ciao", "1");

        assertEquals(BASE_URL + "test/1?ciao=1", service.build());

        service = mService.fromEndpoint("/test/:id")
                .addParameter("id", 1)
                .addParameter("ciao", "1")
                .ignoreUnmatchedParameters();

        assertEquals(BASE_URL + "test/1", service.build());

        service = mService.fromEndpoint("/test/:id")
                .addQueryParameter("id", 1)
                .ignoreUnmatchedParameters();

        assertEquals(BASE_URL + "test/id?id=1", service.build());
    }
}