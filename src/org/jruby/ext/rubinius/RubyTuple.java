/*
 **** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2010 Charles O Nutter <headius@headius.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.ext.rubinius;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.util.RuntimeHelpers;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ArraysUtil;

public class RubyTuple extends RubyObject {
    private IRubyObject[] ary;

    public RubyTuple(Ruby runtime, RubyClass metaclass, int size) {
        super(runtime, metaclass);
        this.ary = new IRubyObject[size];
        RuntimeHelpers.fillNil(ary, runtime);
    }

    public static void createTupleClass(Ruby runtime) {
        RubyClass tupleClass = runtime
                .getOrCreateModule("Rubinius")
                .defineClassUnder("Tuple", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
        tupleClass.setReifiedClass(RubyTuple.class);

        tupleClass.defineAnnotatedMethods(RubyTuple.class);
    }

    @JRubyMethod(name = "new", meta = true)
    public static IRubyObject rbNew(ThreadContext context, IRubyObject tupleCls, IRubyObject cnt) {
        int size = (int)cnt.convertToInteger().getLongValue();
        return new RubyTuple(context.runtime, (RubyClass)tupleCls, size);
    }

    @JRubyMethod(name = "[]")
    public IRubyObject op_aref(ThreadContext context, IRubyObject idx) {
        return ary[(int)((RubyFixnum)idx).getLongValue()];
    }

    @JRubyMethod(name = "[]=")
    public IRubyObject op_aset(ThreadContext context, IRubyObject idx, IRubyObject val) {
        int index = (int)((RubyFixnum)idx).getLongValue();
        if (index >= ary.length) {
            ary = ArraysUtil.copyOf(ary, ary.length * 3 / 2 + 1);
        }
        return ary[index] = val;
    }
}
