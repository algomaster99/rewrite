/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.cleanup

import org.junit.jupiter.api.Test
import org.openrewrite.Recipe
import org.openrewrite.java.JavaRecipeTest

interface BigDecimalRoundingConstantsToEnumsTest : JavaRecipeTest {
    override val recipe: Recipe
        get() = BigDecimalRoundingConstantsToEnums()

    @Test
    fun alibabaDruid() = assertUnchanged(
        moderneAstLink = "https://api.moderne.io/worker/cmVjaXBld29ya2VyLXByb2QtdjExNi1mZDU3/ast/file/alibaba%3Adruid/056645ef88735137f2d063a643304501cb02fba3/c3JjL21haW4vamF2YS9jb20vYWxpYmFiYS9kcnVpZC91dGlsL015U3FsVXRpbHMuamF2YQ=="
    )

    @Test
    fun bigDecimalRoundingNoChange() = assertUnchanged(
        before = """
            import java.math.BigDecimal;import java.math.RoundingMode;class A {
                void divide() {
                    BigDecimal bd = BigDecimal.valueOf(10);
                    BigDecimal bd2 = BigDecimal.valueOf(2);
                    BigDecimal bd3 = bd.divide(bd2, RoundingMode.DOWN);
                    bd3.setScale(2, RoundingMode.HALF_EVEN);
                }
            }
        """
    )

    @Test
    fun bigDecimalRoundingChangeRoundingMode() = assertChanged(
        before = """
            import java.math.BigDecimal;
            
            class A {
                void divide() {
                    BigDecimal bd = BigDecimal.valueOf(10);
                    BigDecimal bd2 = BigDecimal.valueOf(2);
                    BigDecimal bd3 = bd.divide(bd2, BigDecimal.ROUND_DOWN);
                    bd.divide(bd2, 1);
                    bd.divide(bd2, 1, BigDecimal.ROUND_CEILING);
                    bd.divide(bd2, 1, 1);
                    bd.setScale(2, 1);
                }
            }
        """,
        after = """
            import java.math.BigDecimal;
            import java.math.RoundingMode;
            
            class A {
                void divide() {
                    BigDecimal bd = BigDecimal.valueOf(10);
                    BigDecimal bd2 = BigDecimal.valueOf(2);
                    BigDecimal bd3 = bd.divide(bd2, RoundingMode.DOWN);
                    bd.divide(bd2, RoundingMode.DOWN);
                    bd.divide(bd2, 1, RoundingMode.CEILING);
                    bd.divide(bd2, 1, RoundingMode.DOWN);
                    bd.setScale(2, RoundingMode.DOWN);
                }
            }
        """
    )
}
