/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.kotlin

/**
 * Team: NESP Technology
 * Author: <a href="mailto:1756404649@qq.com">JinZhaolu Email:1756404649@qq.com</a>
 * Time: Created 2021/11/21 上午11:08
 * Description:
 **/
fun <T1, T2, R> ((T1, T2) -> R).currying() = { a1: T1 -> { a2: T2 -> this(a1, a2) } }
fun <T1, T2, T3, R> ((T1, T2, T3) -> R).currying() =
    { a1: T1 -> { a2: T2 -> { a3: T3 -> this(a1, a2, a3) } } }

fun <T1, T2, T3, T4, R> ((T1, T2, T3, T4) -> R).currying() =
    { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> this(a1, a2, a3, a4) } } } }

fun <T1, T2, T3, T4, T5, R> ((T1, T2, T3, T4, T5) -> R).currying() =
    { a1: T1 -> { a2: T2 -> { a3: T3 -> { a4: T4 -> { a5: T5 -> this(a1, a2, a3, a4, a5) } } } } }

fun <T1, T2, T3, T4, T5, T6, R> ((T1, T2, T3, T4, T5, T6) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            this(a1, a2, a3, a4, a5, a6)
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, R> ((T1, T2, T3, T4, T5, T6, T7) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                this(a1, a2, a3, a4, a5, a6, a7)
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> ((T1, T2, T3, T4, T5, T6, T7, T8) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    this(a1, a2, a3, a4, a5, a6, a7, a8)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        this(a1, a2, a3, a4, a5, a6, a7, a8, a9)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ((T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                this(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            this(
                                                                a1,
                                                                a2,
                                                                a3,
                                                                a4,
                                                                a5,
                                                                a6,
                                                                a7,
                                                                a8,
                                                                a9,
                                                                a10,
                                                                a11,
                                                                a12,
                                                                a13,
                                                                a14
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                this(
                                                                    a1,
                                                                    a2,
                                                                    a3,
                                                                    a4,
                                                                    a5,
                                                                    a6,
                                                                    a7,
                                                                    a8,
                                                                    a9,
                                                                    a10,
                                                                    a11,
                                                                    a12,
                                                                    a13,
                                                                    a14,
                                                                    a15,
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    this(
                                                                        a1,
                                                                        a2,
                                                                        a3,
                                                                        a4,
                                                                        a5,
                                                                        a6,
                                                                        a7,
                                                                        a8,
                                                                        a9,
                                                                        a10,
                                                                        a11,
                                                                        a12,
                                                                        a13,
                                                                        a14,
                                                                        a15,
                                                                        a16,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    { a17: T17 ->
                                                                        this(
                                                                            a1,
                                                                            a2,
                                                                            a3,
                                                                            a4,
                                                                            a5,
                                                                            a6,
                                                                            a7,
                                                                            a8,
                                                                            a9,
                                                                            a10,
                                                                            a11,
                                                                            a12,
                                                                            a13,
                                                                            a14,
                                                                            a15,
                                                                            a16,
                                                                            a17,
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    { a17: T17 ->
                                                                        { a18: T18 ->
                                                                            this(
                                                                                a1,
                                                                                a2,
                                                                                a3,
                                                                                a4,
                                                                                a5,
                                                                                a6,
                                                                                a7,
                                                                                a8,
                                                                                a9,
                                                                                a10,
                                                                                a11,
                                                                                a12,
                                                                                a13,
                                                                                a14,
                                                                                a15,
                                                                                a16,
                                                                                a17,
                                                                                a18,
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    { a17: T17 ->
                                                                        { a18: T18 ->
                                                                            { a19: T19 ->
                                                                                this(
                                                                                    a1,
                                                                                    a2,
                                                                                    a3,
                                                                                    a4,
                                                                                    a5,
                                                                                    a6,
                                                                                    a7,
                                                                                    a8,
                                                                                    a9,
                                                                                    a10,
                                                                                    a11,
                                                                                    a12,
                                                                                    a13,
                                                                                    a14,
                                                                                    a15,
                                                                                    a16,
                                                                                    a17,
                                                                                    a18,
                                                                                    a19,
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    { a17: T17 ->
                                                                        { a18: T18 ->
                                                                            { a19: T19 ->
                                                                                { a20: T20 ->
                                                                                    this(
                                                                                        a1,
                                                                                        a2,
                                                                                        a3,
                                                                                        a4,
                                                                                        a5,
                                                                                        a6,
                                                                                        a7,
                                                                                        a8,
                                                                                        a9,
                                                                                        a10,
                                                                                        a11,
                                                                                        a12,
                                                                                        a13,
                                                                                        a14,
                                                                                        a15,
                                                                                        a16,
                                                                                        a17,
                                                                                        a18,
                                                                                        a19,
                                                                                        a20,
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20,
        T21, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17,
    T18, T19, T20, T21
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    { a17: T17 ->
                                                                        { a18: T18 ->
                                                                            { a19: T19 ->
                                                                                { a20: T20 ->
                                                                                    { a21: T21 ->
                                                                                        this(
                                                                                            a1,
                                                                                            a2,
                                                                                            a3,
                                                                                            a4,
                                                                                            a5,
                                                                                            a6,
                                                                                            a7,
                                                                                            a8,
                                                                                            a9,
                                                                                            a10,
                                                                                            a11,
                                                                                            a12,
                                                                                            a13,
                                                                                            a14,
                                                                                            a15,
                                                                                            a16,
                                                                                            a17,
                                                                                            a18,
                                                                                            a19,
                                                                                            a20,
                                                                                            a21,
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20,
        T21, T22, R> ((
    T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17,
    T18, T19, T20, T21, T22
) -> R).currying() =
    { a1: T1 ->
        { a2: T2 ->
            { a3: T3 ->
                { a4: T4 ->
                    { a5: T5 ->
                        { a6: T6 ->
                            { a7: T7 ->
                                { a8: T8 ->
                                    { a9: T9 ->
                                        { a10: T10 ->
                                            { a11: T11 ->
                                                { a12: T12 ->
                                                    { a13: T13 ->
                                                        { a14: T14 ->
                                                            { a15: T15 ->
                                                                { a16: T16 ->
                                                                    { a17: T17 ->
                                                                        { a18: T18 ->
                                                                            { a19: T19 ->
                                                                                { a20: T20 ->
                                                                                    { a21: T21 ->
                                                                                        { a22: T22 ->
                                                                                            this(
                                                                                                a1,
                                                                                                a2,
                                                                                                a3,
                                                                                                a4,
                                                                                                a5,
                                                                                                a6,
                                                                                                a7,
                                                                                                a8,
                                                                                                a9,
                                                                                                a10,
                                                                                                a11,
                                                                                                a12,
                                                                                                a13,
                                                                                                a14,
                                                                                                a15,
                                                                                                a16,
                                                                                                a17,
                                                                                                a18,
                                                                                                a19,
                                                                                                a20,
                                                                                                a21,
                                                                                                a22,
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

operator fun <T1, T2, R> ((T1) -> (T2) -> R).invoke(a1: T1, a2: T2) = this(a1)(a2)
operator fun <T1, T2, T3, R> ((T1) -> (T2) -> (T3) -> R).invoke(a1: T1, a2: T2, a3: T3) =
    this(a1)(a2)(a3)

operator fun <T1, T2, T3, T4, R> ((T1) -> (T2) -> (T3) -> (T4) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4
) = this(a1)(a2)(a3)(a4)

operator fun <T1, T2, T3, T4, T5, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
) = this(a1)(a2)(a3)(a4)(a5)

operator fun <T1, T2, T3, T4, T5, T6, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
) = this(a1)(a2)(a3)(a4)(a5)(a6)

operator fun <T1, T2, T3, T4, T5, T6, T7, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6)
-> (T7) -> (T8) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6)
-> (T7) -> (T8) -> (T9) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) ->
    (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ((T1) -> (T2) -> (T3) -> (T4) ->
    (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> ((T1) -> (T2) -> (T3) -> (T4) ->
    (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> (T12) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> ((T1) -> (T2) -> (T3) ->
    (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> (T12) -> (T13) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ((T1) -> (T2) ->
    (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> (T12) -> (T13) ->
    (T14) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ((T1) -> (T2) ->
    (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> (T12) -> (T13) ->
    (T14) -> (T15) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ((T1) ->
    (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> (T12) -> (T13) ->
    (T14) -> (T15) -> (T16) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> ((T1) ->
    (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) -> (T12) -> (T13) ->
    (T14) -> (T15) -> (T16) -> (T17) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
    a17: T17,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R>
        ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) ->
            (T12) -> (T13) -> (T14) -> (T15) -> (T16) -> (T17) -> (T18) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
    a17: T17,
    a18: T18,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18,
        T19, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) -> (T10) -> (T11) ->
    (T12) -> (T13) -> (T14) -> (T15) -> (T16) -> (T17) -> (T18) -> (T19) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
    a17: T17,
    a18: T18,
    a19: T19,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18,
        T19, T20, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) ->
    (T10) -> (T11) -> (T12) -> (T13) -> (T14) -> (T15) -> (T16) -> (T17) -> (T18) -> (T19) -> (T20) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
    a17: T17,
    a18: T18,
    a19: T19,
    a20: T20,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18,
        T19, T20, T21, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) -> (T9) ->
    (T10) -> (T11) -> (T12) -> (T13) -> (T14) -> (T15) -> (T16) -> (T17) -> (T18) -> (T19) ->
    (T20) -> (T21) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
    a17: T17,
    a18: T18,
    a19: T19,
    a20: T20,
    a21: T21,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(
    a21
)

operator fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18,
        T19, T20, T21, T22, R> ((T1) -> (T2) -> (T3) -> (T4) -> (T5) -> (T6) -> (T7) -> (T8) ->
    (T9) -> (T10) -> (T11) -> (T12) -> (T13) -> (T14) -> (T15) -> (T16) -> (T17) -> (T18) -> (T19) ->
    (T20) -> (T21) -> (T22) -> R).invoke(
    a1: T1,
    a2: T2,
    a3: T3,
    a4: T4,
    a5: T5,
    a6: T6,
    a7: T7,
    a8: T8,
    a9: T9,
    a10: T10,
    a11: T11,
    a12: T12,
    a13: T13,
    a14: T14,
    a15: T15,
    a16: T16,
    a17: T17,
    a18: T18,
    a19: T19,
    a20: T20,
    a21: T21,
    a22: T22,
) = this(a1)(a2)(a3)(a4)(a5)(a6)(a7)(a8)(a9)(a10)(a11)(a12)(a13)(a14)(a15)(a16)(a17)(a18)(a19)(a20)(
    a21
)(a22)