package com.trite.apps.flowbot.processorcore;

import com.trite.apps.flowbot.result.Result;

public interface Runnable {
     Result run(String stepName, Result[] flowResults) throws Exception;

}
