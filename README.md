# hive-blocks
The project supports running conditional blocks with hive queries

You can provide it a xml in below construct:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<blocks name="hive-blocks" base-path="C:/Users/manojkumar.vohra/Desktop/hblocks/">
  <block block-id="0">
    <variable name="b1Var1" type="int"/>
    <variable name="b1Var2" type="string"/>
    <variable name="addVal" type="int"/>
    <export execution-order="0" query-file="export_vars.hql"/>
    <if execution-order="1" condition=":b1Var1 > 2">
      <query execution-order="0" query-file="pre_if.hql" />
      <if execution-order="1" condition=":b1Var2 == 'abc'">
      	<query execution-order="0" query-file="insert_into_dummy.hql" />
      </if>
      <query execution-order="2" query-file="post_if.hql" />
    </if>
    <export execution-order="2" query-file="add_to_vars.hql"/>
    <query execution-order="3" query-file="insert_into_dummy_plus.hql" />
  </block>
</blocks>
```

Note: The supporting files for above block configuration can be found in sample folder in project.
(Execution log can be seen in sample folder)

- Here the execution can be subdivided into blocks where each block signify a pl/sql block with multiple IFs, Queries or nesting of that with in each other.
- The execution order of blocks is determined by 'block-id' of each sub block element.
- The execution order of elements (at same level) within block is determined by 'execution-order' attribute of elements.
- With in elements the execution order of nested elements (at same level) is determined by 'execution-order' attribute of elements.
- A \<block\> signify a piece of confined executible. It can contain:
  -  \<variable\>
  -   \<export\>
  -   \<if\>
  -   \<query\>
- A \<variable\> signify a variable which can be assigned a value using \<export\> elements.
  -   \<variable\> remain valid at block level.
  -   You can have variable with same names between different blocks.
  -   Variable can referenced within query or If conditions by prepending ':' in front of variable name.
- A \<export\> signify a query whose result set is assigned to variables.
- A \<if\> block signify a conditional block whose execution depends on its condition. It can contain:
  -   \<export\>
  -   \<if\>
  -   \<query\>

Requirements
------------
JDK 1.8
MVN 3
