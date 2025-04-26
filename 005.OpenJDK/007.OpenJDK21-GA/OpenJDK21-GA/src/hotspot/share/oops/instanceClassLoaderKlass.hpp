/*
 * Copyright (c) 2011, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#ifndef SHARE_OOPS_INSTANCECLASSLOADERKLASS_HPP
#define SHARE_OOPS_INSTANCECLASSLOADERKLASS_HPP

#include "oops/instanceKlass.hpp"
#include "utilities/macros.hpp"

class ClassFileParser;

/**
 * An InstanceClassLoaderKlass is a specialization of the InstanceKlass. It does
 * not add any field.  It is added to walk the dependencies for the class loader
 * key that this class loader points to.  This is how the loader_data graph is
 * walked and dependent class loaders are kept alive.  I thought we walked
 * the list later?
 * InstanceClassLoaderKlass 是 InstanceKlass
 * 的一个特例。它不添加任何字段。它被添加来遍历该类加载器所指向的类加载器键的依赖项。这就是遍历
 * loader_data 图并保持依赖类加载器活动的方式。我以为我们稍后会遍历列表？
 *
 *
 * InstanceClassLoaderKlass类没有添加新的字段，但增加了新的oop遍历方法，在垃圾回收阶段遍历类加载器加载的所有类来标记引用的所有对象
 */

class InstanceClassLoaderKlass: public InstanceKlass {
  friend class VMStructs;
  friend class InstanceKlass;
public:
  static const KlassKind Kind = InstanceClassLoaderKlassKind;

private:
  InstanceClassLoaderKlass(const ClassFileParser& parser) : InstanceKlass(parser, Kind) {}

public:
  InstanceClassLoaderKlass() { assert(DumpSharedSpaces || UseSharedSpaces, "only for CDS"); }

  // Oop fields (and metadata) iterators
  //
  // The InstanceClassLoaderKlass iterators also visit the CLD pointer

  // Forward iteration
  // Iterate over the oop fields and metadata.
  template <typename T, class OopClosureType>
  inline void oop_oop_iterate(oop obj, OopClosureType* closure);

  // Reverse iteration
  // Iterate over the oop fields and metadata.
  template <typename T, class OopClosureType>
  inline void oop_oop_iterate_reverse(oop obj, OopClosureType* closure);

  // Bounded range iteration
  // Iterate over the oop fields and metadata.
  template <typename T, class OopClosureType>
  inline void oop_oop_iterate_bounded(oop obj, OopClosureType* closure, MemRegion mr);
};

#endif // SHARE_OOPS_INSTANCECLASSLOADERKLASS_HPP
