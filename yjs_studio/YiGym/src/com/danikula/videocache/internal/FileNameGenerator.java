package com.danikula.videocache.internal;

import java.io.File;

/**
 * Generator for files to be used for caching.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface FileNameGenerator {

    File generate(String url);

}
