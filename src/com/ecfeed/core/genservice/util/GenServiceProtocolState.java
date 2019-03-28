package com.ecfeed.core.genservice.util;

public enum GenServiceProtocolState {

    BEFORE_BEG_DATA,
    AFTER_BEG_DATA,
    AFTER_BEG_CHUNK,
    AFTER_END_CHUNK,
    AFTER_END_DATA
}
