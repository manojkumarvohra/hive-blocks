# hive-blocks
The project supports running conditional blocks with hive queries

You can provide it a xml in below construct:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<blocks id="hive-blocks" base-path="C:/Users/manojkumar.vohra/Desktop/hblocks/">
  <block id="first-block">
    
    <variable name="b1Var1" type="int"/>
    <variable name="b1Var2" type="string"/>
    <variable name="addVal" type="int"/>
    
    <export id="export_both_vars" query-file="export_vars.hql"/>
    
    <if id="if_gt_2" condition=":b1Var1 > 2 AND :b1Var2 == 'abc'">
    
        <variable name="b1Var2" type="string" value="overriden-b1Var2"/>
    
        <query id="pre_sub_if" query-file="pre_if.hql" />
        <if id="sub_if" condition=":b1Var2 == 'overriden-b1Var2'">
      	     <query id="insert_dummy" query-file="insert_into_dummy.hql" />
        </if>
        <query id="post_if" query-file="post_if.hql" />
        
        <for id="for each dummy value" query-file="iterate_dummy.hql">
          <variable name="dummyfield" type="string"/>
          <print text="Got Dummy Value=:dummyfield"/>
        </for>    
    </if>
    
    <export id="add vars and prepare new" query-file="add_to_vars.hql"/>
    <query id="last insert" query-file="insert_into_dummy_plus.hql" />
  
  </block>
</blocks>
```

Note: The supporting files for above block configuration can be found in sample folder in project.
(Execution log can be seen in sample folder)

- Here the execution can be subdivided into blocks where each block signify a pl/sql block with multiple IFs, Loops, Queries or nesting of that with in each other.
- The execution order of blocks is determined by order they appear in input xml config.
- The execution order of elements (at same level) within block is determined by order they appear.
- With in elements the execution order of nested elements (at same level) is determined by order they appear.
- A \<block\> signify a piece of confined executible. It can contain:
  -  \<variable\>
  -  \<export\>
  -  \<if\>
  -  \<for\>
  -  \<query\>
  -  \<print\>
- A \<variable\> signify a variable which can be assigned a value using \<export\> elements.
  -   \<variable\> defined at block level remain valid at block level
  -   Block level variables can be overridden by defining variable by same name in \<if\> or \<for\>
  -   You can have variable with same names between different blocks.
  -   Variable can referenced within query or If conditions by prepending ':' in front of variable name.
  -   Variable substitution works by first getting values within immediate element (\<if\> or \<for\>), If no value found the search for variable value proceeds to element's parent until it reaches the block level variable look up.
- A \<export\> signify a query whose result set is assigned to variables.
- A \<if\> block signify a conditional block whose execution depends on its condition. It can contain:
 -    \<variable\>
  -   \<export\> (export would assign values to variable defined in IF block)
  -   \<if\>
  -   \<query\>
  -   \<for\>
  -   \<print\>
- A \<for\> block signify a for loop whose execution depends on the rows returned by executing query in queryfile. It can contain:
  -   \<variable\>
  -   \<export\> (export would assign values to variable defined in FOR block)
  -   \<if\>
  -   \<query\>
  -   \<for\>
  -   \<print\>
- A \<print\> signify a text/comment which can be printed with in execution for information purpose. Text can contain variables in format :variableName

Requirements
------------
JDK 1.8
MVN 3
