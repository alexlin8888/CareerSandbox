package com.careersandbox.app.ui.screens.workplace

import com.careersandbox.app.R

/* =====================================================================
   SandboxFaces —— 角色表情對應
   依「使用者的選擇結果(repDelta)」即時套用反應表情;沒反應時依累積計量挑底表情。
   Ken 用滿 8 種,其他角色用各自的表情組,讓臉真的會隨回答變。
   ===================================================================== */

/* Ken 反應表情(選擇後)：大加分→大笑,加分→開心,扣分→皺眉,大扣分→不悅 */
fun faceKenReact(delta: Int, managerTrust: Int): Int = when {
    delta >= 2 -> R.drawable.ken_laughing
    delta == 1 -> R.drawable.ken_happy
    delta == -1 -> R.drawable.ken_concerned
    delta <= -2 -> R.drawable.ken_angry
    else -> faceKenBase(managerTrust)
}

/* Ken 底表情(未選擇時)：依累積主管信任 */
fun faceKenBase(managerTrust: Int): Int = when {
    managerTrust >= 7 -> R.drawable.ken_happy
    managerTrust >= 5 -> R.drawable.ken_soft
    managerTrust >= 3 -> R.drawable.ken_neutral
    managerTrust >= 2 -> R.drawable.ken_concerned
    else -> R.drawable.ken_stern
}

/* Vivian：滿意 / 不悅 / 中性 */
fun faceVivian(delta: Int): Int = when {
    delta >= 1 -> R.drawable.colleague_vivian_satisfied
    delta <= -1 -> R.drawable.colleague_vivian_displeased
    else -> R.drawable.colleague_vivian
}

/* 阿哲：不爽 / 平靜 */
fun faceAkai(delta: Int): Int = when {
    delta <= -1 -> R.drawable.colleague_akai_frustrated
    else -> R.drawable.colleague_akai_calm
}

/* 小芳：欣慰 / 擔心 / 中性 */
fun faceFang(delta: Int): Int = when {
    delta >= 1 -> R.drawable.fang_pleased
    delta <= -1 -> R.drawable.fang_worried
    else -> R.drawable.fang_neutral
}
