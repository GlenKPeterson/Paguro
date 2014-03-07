// Copyright 2014-03-02 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import java.io.PrintStream;
import java.io.PrintWriter;

public class WrappedException extends RuntimeException {

    private final Throwable t;

    private WrappedException(Throwable throwable) { t = throwable; }

    public static RuntimeException of(Throwable throwable) {
        if (throwable == null) {
            throw new IllegalArgumentException("Null exception");
        }
        if (throwable instanceof RuntimeException) {
            // Don't wrap stuff we don't have to.
            return (RuntimeException) throwable;
        }
        return new WrappedException(throwable);
    }

    @Override
    public String getMessage() { return t.getMessage(); }

    @Override
    public String getLocalizedMessage() { return t.getLocalizedMessage(); }

    @Override
    public synchronized Throwable getCause() { return t.getCause(); }

    @Override
    public synchronized Throwable initCause(Throwable cause) { return t.initCause(cause); }

    @Override
    public int hashCode() { return t.hashCode(); }

//    /**
//     This implementation of equals is wrong because it is not reflexive (meaning this.equals(that)
//     does not imply that that.equals(this) and vice versa.  Maybe it's better not to implement
//     this method and stick with referential equality?  Feedback would be appreciated.
//     @param other the object to compare the wrapped exception to.
//     @return
//     */
//    @Override
//    public boolean equals(Object other) {
//        if (this == other) { return true; }
//
//        if ( (other == null) ||
//             !(other instanceof Throwable) ||
//             (this.hashCode() != other.hashCode()) ) {
//            return false;
//        }
//        return t.equals(other);
//    }

    @Override
    public String toString() { return t.toString(); }

    @Override
    public void printStackTrace() { t.printStackTrace(); }

    @Override
    public void printStackTrace(PrintStream s) { t.printStackTrace(s); }

    @Override
    public void printStackTrace(PrintWriter s) { t.printStackTrace(s); }

    @Override
    public synchronized Throwable fillInStackTrace() { return t.fillInStackTrace(); }

    @Override
    public StackTraceElement[] getStackTrace() { return t.getStackTrace(); }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) { t.setStackTrace(stackTrace); }
}
