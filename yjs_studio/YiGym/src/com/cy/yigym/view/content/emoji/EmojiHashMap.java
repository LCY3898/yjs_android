package com.cy.yigym.view.content.emoji;

import java.util.LinkedHashMap;

import com.efit.sport.R;

/**
 * Caiyuan Huang
 * <p>
 * 2015-8-5
 * </p>
 * <p>
 * 表情映射表
 * </p>
 */
public class EmojiHashMap {
	private static LinkedHashMap<String, Integer> emojiMap = new LinkedHashMap<String, Integer>();
	static {
		put("[憨笑]", R.drawable.f001);
		put("[偷笑]", R.drawable.f002);
		put("[大哭]", R.drawable.f003);
		put("[发怒]", R.drawable.f004);
		put("[微笑]", R.drawable.f005);
		put("[流泪]", R.drawable.f006);
		put("[流汗]", R.drawable.f007);
		put("[呲牙]", R.drawable.f008);
		put("[晕]", R.drawable.f009);
		put("[傲慢]", R.drawable.f010);
		put("[色]", R.drawable.f011);
		put("[快哭了]", R.drawable.f012);
		put("[亲亲]", R.drawable.f013);
		put("[抓狂]", R.drawable.f014);
		put("[抠鼻]", R.drawable.f015);
		put("[震惊]", R.drawable.f016);
		put("[鼓掌]", R.drawable.f017);
		put("[坏笑]", R.drawable.f018);
		put("[鄙视]", R.drawable.f019);
		put("[惊恐]", R.drawable.f020);
		put("[委屈]", R.drawable.f021);
		put("[睡觉]", R.drawable.f022);
		put("[困]", R.drawable.f023);
		put("[衰]", R.drawable.f024);
		put("[疑问]", R.drawable.f025);
		put("[调皮]", R.drawable.f026);
		put("[敲打]", R.drawable.f027);
		put("[吐]", R.drawable.f028);
		put("[害羞]", R.drawable.f029);
		put("[闭嘴]", R.drawable.f030);
		put("[阴险]", R.drawable.f031);
		put("[尴尬]", R.drawable.f032);
		put("[右哼哼]", R.drawable.f033);
		put("[左哼哼]", R.drawable.f034);
		put("[嘘]", R.drawable.f035);
		put("[爱财]", R.drawable.f036);
		put("[得意]", R.drawable.f037);
		put("[白眼]", R.drawable.f038);
		put("[糗大了]", R.drawable.f039);
		put("[美味]", R.drawable.f040);
		put("[哈欠]", R.drawable.f041);
		put("[咒骂]", R.drawable.f042);
		put("[可怜]", R.drawable.f043);
		put("[惊吓]", R.drawable.f044);
		put("[难过]", R.drawable.f045);
		put("[奋斗]", R.drawable.f046);
		put("[猪头]", R.drawable.f047);
		put("[炸弹]", R.drawable.f048);
		put("[咖啡]", R.drawable.f049);
		put("[礼品]", R.drawable.f050);
		put("[玫瑰]", R.drawable.f051);
		put("[凋谢]", R.drawable.f052);
		put("[蜡烛]", R.drawable.f053);
		put("[爱心]", R.drawable.f054);
		put("[心碎]", R.drawable.f055);
		put("[示爱]", R.drawable.f056);
		put("[太阳]", R.drawable.f057);
		put("[月亮]", R.drawable.f058);
		put("[蛋糕]", R.drawable.f059);
		put("[闪电]", R.drawable.f060);
		put("[OK]", R.drawable.f061);
		put("[勾引]", R.drawable.f062);
		put("[强]", R.drawable.f063);
		put("[弱]", R.drawable.f064);
		put("[胜利]", R.drawable.f065);
		put("[抱拳]", R.drawable.f066);
		put("[握手]", R.drawable.f067);
		put("[刀]", R.drawable.f068);
		put("[喝彩]", R.drawable.f069);
		put("[篮球]", R.drawable.f070);
		put("[足球]", R.drawable.f071);
		put("[乒乓]", R.drawable.f072);
		put("[米饭]", R.drawable.f073);
		put("[西瓜]", R.drawable.f074);
		put("[啤酒]", R.drawable.f075);
		put("[便便]", R.drawable.f076);
		put("[飞机]", R.drawable.f077);
		put("[金钱]", R.drawable.f078);
		put("[抱抱]", R.drawable.f079);
		put("[可爱]", R.drawable.f080);
		put("[撇嘴]", R.drawable.f081);
		put("[酷]", R.drawable.f082);
		put("[冷汗]", R.drawable.f083);
		put("[折磨]", R.drawable.f084);
		put("[骷髅头]", R.drawable.f085);
		put("[再见]", R.drawable.f086);
		put("[擦汗]", R.drawable.f087);
		put("[吓]", R.drawable.f090);
		put("[菜刀]", R.drawable.f092);
		put("[拳头]", R.drawable.f093);
		put("[差劲]", R.drawable.f094);
		put("[爱你]", R.drawable.f095);
		put("[NO]", R.drawable.f096);
		put("[飞吻]", R.drawable.f097);
		put("[接吻]", R.drawable.f098);
		put("[发抖]", R.drawable.f099);
		put("[呕火]", R.drawable.f100);
		put("[点头]", R.drawable.f101);
		put("[回头]", R.drawable.f102);
		put("[挥手帕]", R.drawable.f103);
		put("[激动]", R.drawable.f104);
		put("[跳跳]", R.drawable.f105);
		put("[左太极]", R.drawable.f106);
		put("[右太极]", R.drawable.f107);
		put("[打太极]", R.drawable.f108);

	}

	public static void put(String emojiKey, Integer drawableId) {
		emojiMap.put(emojiKey, drawableId);
	}

	public static Integer get(String emojiKey) {
		return emojiMap.get(emojiKey);
	}

	public static LinkedHashMap<String, Integer> getMap() {
		return emojiMap;
	}
}
