module com.acromere.zerra {

	requires static lombok;
	requires static org.jspecify;

	requires com.acromere.zevra;
	requires javafx.controls;
	requires javafx.swing;
	requires image4j;

	exports com.acromere.zerra.color;
	exports com.acromere.zerra.event;
	exports com.acromere.zerra.font;
	exports com.acromere.zerra.image;
	exports com.acromere.zerra.javafx;
	exports com.acromere.zerra.stage;
	exports com.acromere.zerra.theme;
	exports com.acromere.zerra;
}
