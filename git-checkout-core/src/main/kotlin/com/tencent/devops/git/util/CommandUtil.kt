/*
 * Tencent is pleased to support the open source community by making BK-CI 蓝鲸持续集成平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CI 蓝鲸持续集成平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.devops.git.util

import com.tencent.bk.devops.plugin.pojo.ErrorType
import com.tencent.bk.devops.plugin.script.SensitiveLineParser
import com.tencent.devops.git.constant.GitConstants
import com.tencent.devops.git.enums.GitErrors
import com.tencent.devops.git.exception.GitExecuteException
import com.tencent.devops.git.log.GitLogOutputStream
import com.tencent.devops.git.log.LogType
import com.tencent.devops.git.pojo.GitOutput
import java.io.File
import java.io.InputStream
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.ExecuteException
import org.apache.commons.exec.PumpStreamHandler
import org.apache.commons.exec.environment.EnvironmentUtils
import org.apache.commons.io.IOUtils

object CommandUtil {

    @SuppressWarnings("LongParameterList")
    fun execute(
        workingDirectory: File? = null,
        executable: String,
        args: List<String>,
        runtimeEnv: Map<String, String> = mapOf(),
        inputStream: InputStream? = null,
        allowAllExitCodes: Boolean = false,
        logType: LogType = LogType.TEXT,
        printLogger: Boolean = true
    ): GitOutput {
        val executor = DefaultExecutor()
        executor.workingDirectory = workingDirectory
        val stdOuts = mutableListOf<String>()
        val errOuts = mutableListOf<String>()
        val outputStream = object : GitLogOutputStream(logType) {
            override fun processLine(line: String?, level: Int) {
                if (line == null) {
                    return
                }
                val tmpLine = SensitiveLineParser.onParseLine(line)
                if (printLogger) {
                    println("  $tmpLine")
                }
                stdOuts.add(tmpLine)
            }
        }

        val errorStream = object : GitLogOutputStream(logType) {
            override fun processLine(line: String?, level: Int) {
                if (line == null) {
                    return
                }
                val tmpLine = SensitiveLineParser.onParseLine(line)
                if (printLogger) {
                    System.err.println("  $tmpLine")
                }
                errOuts.add(tmpLine)
            }
        }
        executor.streamHandler = PumpStreamHandler(outputStream, errorStream, inputStream)
        if (allowAllExitCodes) {
            executor.setExitValues(null)
        }
        val command = CommandLine.parse(executable).addArguments(args.toTypedArray(), false)
        if (printLogger) {
            println("##[command]$ ${command.toStrings().joinToString(" ")}")
        }
        try {
            // 系统环境变量 + 运行时环境变量
            val env = EnvironmentUtils.getProcEnvironment()
            env.putAll(runtimeEnv)
            val exitCode = executor.execute(command, env)
            return GitOutput(stdOuts = stdOuts, errOuts = errOuts, exitCode = exitCode)
        } catch (ignore: ExecuteException) {
            val gitErrors = parseError(stdOuts.plus(errOuts))
            val errorMsg = gitErrors?.description ?: "exec ${command.toStrings().joinToString(" ")} failed"
            val errorCode = gitErrors?.errorCode ?: GitConstants.CONFIG_ERROR
            val errorType = gitErrors?.errorType ?: ErrorType.USER
            throw GitExecuteException(errorType = errorType, errorCode = errorCode, errorMsg = errorMsg)
        } catch (ignore: Throwable) {
            throw GitExecuteException(
                errorType = ErrorType.PLUGIN,
                errorCode = GitConstants.DEFAULT_ERROR,
                errorMsg = ignore.message ?: ""
            )
        } finally {
            IOUtils.close(errorStream, outputStream, inputStream)
        }
    }

    private fun parseError(stdErr: List<String>): GitErrors? {
        // 反向遍历,异常基本都是在最后几条
        stdErr.asReversed().forEach { err ->
            for (gitError in GitErrors.values()) {
                if (gitError.regex.matches(err)) {
                    return gitError
                }
            }
        }
        return null
    }
}