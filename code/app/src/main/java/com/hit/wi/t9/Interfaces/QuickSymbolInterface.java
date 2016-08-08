package com.hit.wi.t9.Interfaces;

import com.hit.wi.t9.values.QuickSymbolsDataStruct;

import java.io.IOException;

/**
 * Created by Administrator on 2015/6/30.
 */
public interface QuickSymbolInterface {
    QuickSymbolsDataStruct loadSymbolFromFile(String symbolModeName) throws IOException;

    void writeSymbolsToFile(String symbolModeName, QuickSymbolsDataStruct symbolsDataStruct) throws IOException;
}
