 package me.pdthx;
    import android.content.Context;
    import android.text.TextUtils;
    import android.util.AttributeSet;
    import android.widget.AutoCompleteTextView;
     
    public class CustomAutoComplete extends AutoCompleteTextView {
        private String previous = "";
        private String seperator = ";";
       public CustomAutoComplete(final Context context, final AttributeSet attrs, final int defStyle) {
           super(context, attrs, defStyle);
           this.setThreshold(0);
       }
       public CustomAutoComplete(final Context context, final AttributeSet attrs) {
           super(context, attrs);
          this.setThreshold(0);
      }
      public CustomAutoComplete(final Context context) {
           super(context);
          this.setThreshold(0);
       }
      /**
       * This method filters out the existing text till the separator
       * and launched the filtering process again
       */
      @Override
      protected void performFiltering(final CharSequence text, final int keyCode) {
          String filterText = text.toString().trim();
         previous = filterText.substring(0,filterText.lastIndexOf(getSeperator())+1);
         filterText = filterText.substring(filterText.lastIndexOf(getSeperator()) + 1);
         if(!TextUtils.isEmpty(filterText)){
             super.performFiltering(filterText, keyCode);
           }
       }
       /**
        * After a selection, capture the new value and append to the existing
        * text
        */
       @Override
       protected void replaceText(final CharSequence text) {
           super.replaceText(previous+text+getSeperator());
       }
       public String getSeperator() {
           return seperator;
       }
       public void setSeperator(final String seperator) {
           this.seperator = seperator;
      }
   }