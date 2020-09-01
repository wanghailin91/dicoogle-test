var isMoving = false; //variable to determine if element is moving.
(function($) {

    var onTS;
    var onTM;
    var onTE;
    var can;

    $.fn.rasterDrag = function(options) {
        if(!wlwwflage){
        dragtool=true;
        //Sets up Defaults Object
        var defaults = {
            onDrop: null,
            minMove: 20
        }; // end defaults
   
        //Combines defaults with your options  
        var opts = $.extend({}, defaults, options);
        
        $(this).each(function(){
            //sets up position object to store event position data
            var pos = {
                startX: null,
                startY: null,
                curX: null,// keeps touch X position while moving on the screen
                curY: null, // keeps touch Y position while moving on the screen
                lastTouchX: null,
                lastTouchY: null,
                offsetX: null,
                offsetY: null
            };
            onTS = function onTouchStart(e){
                if(e.touches.length==1){
                    //set event position data on touchstart
                    pos.startX = e.touches[0].pageX;
                    pos.startY = e.touches[0].pageY;
                    pos.curX = pos.startX;
                    pos.curY = pos.startY;
                    pos.lastTouchX = pos.curX;// 
                    pos.lastTouchY = pos.curY;
                    isMoving = true;//set up touchmove event
                    this.removeEventListener('touchmove', onTM, false);
                    this.addEventListener('touchmove', onTM, false);
                    pos.offsetY  = this.offsetTop;//store element offset values
                    pos.offsetX = this.offsetLeft;
                }
            };
            onTM = function onTouchMove(e)
            {
                e.preventDefault()//prevent scrolling 
                if(e.touches.length==1)
                {
                    if (isMoving)
                    {
                        //console.log("......:"+e.touches.length);
                        //update elements current position
                        pos.curX = e.touches[0].pageX;
                        pos.curY = e.touches[0].pageY;
                        //positions element during move
                        var spanX = (pos.curX - pos.lastTouchX);
                        var spanY = (pos.curY - pos.lastTouchY);
                        //allow absolute positioning.
                        $(this).css("position", "absolute");
                        // set z-index
                        $(this).css("z-index", "10000");
                        //updates elements CSS as it is dragged
                        $(this).css({
                            "left": (spanX + pos.offsetX) + "px", 
                            "top": (spanY+ pos.offsetY)+ "px"
                        });
                    }
                }
            };
            onTE = function onTouchEnd(e)
            {
                if(e.touches.length==1){
                    //calculates if minimum drag requirement is met
                    if ((Math.abs(pos.curX - pos.startX) < opts.minMove) && (Math.abs(pos.curY - pos.startY) < opts.minMove))  {
                    //do nothing
                    }
                    else {
                        this.removeEventListener('touchmove', onTM, false);
                        isMoving = false;
                        opts.onDrop();
                    }
                }
		isMoving = false;
            };
            //console.log(onTE);
            //Set up touch event listeners. 
            this.removeEventListener('touchstart', onTS, false);
            this.removeEventListener('touchend', onTE, false);
            this.addEventListener('touchstart', onTS, false);
            this.addEventListener('touchend', onTE, false);
            
            can=this;
        });  
        }
    }
            
    $.fn.unBindDrag = function() {
        if(dragtool){
            dragtool=false;
        can.removeEventListener('touchstart', onTS, false);
        can.removeEventListener('touchmove', onTM, false);
        can.removeEventListener('touchend', onTE, false);
        }
    }

})(jQuery);

// $('#myCanvas').unBindDrag();
