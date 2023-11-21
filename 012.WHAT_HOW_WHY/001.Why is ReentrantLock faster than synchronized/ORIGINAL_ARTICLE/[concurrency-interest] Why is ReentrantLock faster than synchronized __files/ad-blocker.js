    window.googletag = window.googletag || {
        cmd: []
    };
    
	//var GAM_web_interstitial;
	
	
	
	nk.ads_refreshable_queue = [];
	
	
	
	
	
	
	
	
	
    $( document ).ready(function() {
    googletag.cmd.push(function() {
		
		/*
		var topic = "hash-" + ((nk.conf.thread_hash.charCodeAt(0) + nk.conf.thread_hash.charCodeAt(1) + nk.conf.thread_hash.charCodeAt(2) + nk.conf.thread_hash.charCodeAt(3)) % 25);

		googletag.pubads().setTargeting(topic, nk.conf.thread_hash);
		*/
		//GAM_web_interstitial = googletag.defineOutOfPageSlot('/2507246/KIV//narkive//misc//interstitial', googletag.enums.OutOfPageFormat.INTERSTITIAL).addService(googletag.pubads());


        // desktop
        if (1 && (adsense_dispatcher_id == 44 || adsense_dispatcher_id == 45 || adsense_dispatcher_id == 46)) {
			
			
			//$( window ).resize(function() {
				
				if(adsense_dispatcher_id == 46) {
					
					if(nk.playbuzz_id.length > 10) {
						(function (d, s, n) { var js, fjs = d.getElementsByTagName(s)[0]; js = d.createElement(s); js.className = n; js.src = "//player.ex.co/player/" + nk.playbuzz_id; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'exco-player'));
					}
					

				}
				
				else {
				  
				  sidebar_banner_right_max_height = $(window).height() - 60;
				  sidebar_banner_right_max_width = $(window).width() - $('#sidebar_banner_right').offset().left;
			  
				  if($('#sidebar_banner_right').offset().top > 100)
				  {
					  sidebar_banner_right_max_height = 0;
					  sidebar_banner_right_max_width = 0;
				  }
				  
				  sidebar_banner_right_max_height -= 10;
				  sidebar_banner_right_max_width -= 10;
				  
				  
				  sidebar_banner_max_height = $(window).height() - $('.sidebar_banner_placeholder_1').offset().top;
				  if(window.innerWidth > 1100) {
					  sidebar_banner_max_width = 340
				  }
				  else if(window.innerWidth > 1000) {
					  sidebar_banner_max_width = 220;
				  }
				  else {
					  sidebar_banner_max_width = 0;
				  }
				  
				  sidebar_banner_max_height -= 10;
				  /*
				  $('.sidebar_banner_placeholder_1').css('width', '0px');
				  $('.sidebar_banner_placeholder_1').css('height', '0px');
				  $('.sidebar_banner_placeholder_2').css('width', '0px');
				  $('.sidebar_banner_placeholder_2').css('height', '0px');
				  */
				  
				  sidebar_all_sizes = [[300, 600], [300, 250], [160, 600], [336, 280], [120, 600], [250, 250], [240, 400],  [200, 200], [320, 480]];
				  sidebar_win_sizes = ['fluid'];
				  
				  
				  //sidebar_all_sizes = [[123, 123]];
				  //sidebar_win_sizes = [];



				  
					
				  if(sidebar_banner_right_max_height > 480 && sidebar_banner_right_max_width > 300) {
					  
						//$('.sidebar_banner_placeholder_2').css('border', '1px solid red');	 
						$('.sidebar_banner_placeholder_2').css('width', sidebar_banner_right_max_width + 'px');	
						$('.sidebar_banner_placeholder_2').css('height', sidebar_banner_right_max_height + 'px');	 
						
						$('#sidebar_banner_right').css('width', sidebar_banner_right_max_width + 'px');	
						$('#sidebar_banner_right').css('height', sidebar_banner_right_max_height + 'px');	 
						
						sidebar_win_sizes = ['fluid'];
						
						for(i = 0; i < sidebar_all_sizes.length; i++)
						{
							if(sidebar_all_sizes[i][0] <= sidebar_banner_max_width && sidebar_all_sizes[i][1] <= sidebar_banner_max_height)
								sidebar_win_sizes.push(sidebar_all_sizes[i]);
						}
						
						$('.sidebar_banner_placeholder_2').attr('id', 'div-gpt-ad-1628442403237-0');
						$('.sidebar_banner_placeholder_2').prop('id', 'div-gpt-ad-1628442403237-0');
						googletag.defineSlot('/22218282545/desktop//sidebar', sidebar_win_sizes, 'div-gpt-ad-1628442403237-0').addService(googletag.pubads());
						 
					  
				  } else {
					  //$('.sidebar_banner_placeholder_1').css('border', '1px solid red');	 
					  $('.sidebar_banner_placeholder_1').css('width', sidebar_banner_max_width + 'px');	
					  $('.sidebar_banner_placeholder_1').css('height', sidebar_banner_max_height + 'px');	  
					  
					  $('#sidebar_banner_placeholder_1_outer').css('width', sidebar_banner_max_width + 'px');	
					  $('#sidebar_banner_placeholder_1_outer').css('height', sidebar_banner_max_height + 'px');	  
					  
					  if(sidebar_banner_max_width == 340)
					  	$('#sidebar_banner_placeholder_1_outer').css('margin-left', '-100px');
					  else
					  	$('#sidebar_banner_placeholder_1_outer').css('margin-left', '0');	
						  
					  sidebar_win_sizes = ['fluid'];
					  
					  for(i = 0; i < sidebar_all_sizes.length; i++)
					  {
						  if(sidebar_all_sizes[i][0] <= sidebar_banner_max_width && sidebar_all_sizes[i][1] <= sidebar_banner_max_height)
							  sidebar_win_sizes.push(sidebar_all_sizes[i]);
					  }
						  
					  $('.sidebar_banner_placeholder_1').attr('id', 'div-gpt-ad-1628442403237-0');
					  $('.sidebar_banner_placeholder_1').prop('id', 'div-gpt-ad-1628442403237-0');
					  googletag.defineSlot('/22218282545/desktop//sidebar', sidebar_win_sizes, 'div-gpt-ad-1628442403237-0').addService(googletag.pubads());
				  }
				  
			  }
				  
			//	});
				
				
			/*
			
			$( window ).resize(function() {
				  
				  sidebar_banner_right_max_height = $(window).height() - 20;
				  sidebar_banner_right_max_width = $(window).width() - $('#sidebar_banner_right').offset().left;
			  
				  if($('#sidebar_banner_right').offset().top > 100)
				  {
					  sidebar_banner_right_max_height = 0;
					  sidebar_banner_right_max_width = 0;
				  }
				  
				  
				  if(sidebar_banner_right_max_height > 480 && sidebar_banner_right_max_width > 300) {
					  
						$('.sidebar_banner_placeholder_2').css('border', '1px solid red');	 
						$('.sidebar_banner_placeholder_2').css('width', sidebar_banner_right_max_width + 'px');	
						$('.sidebar_banner_placeholder_2').css('height', sidebar_banner_right_max_height + 'px');	                
						 
					  
					  
				  }
				  
				  
				});
	        
	        */
	        /*
	        sidebar_banner_right_max_height = $(window).height() - 300;
		    sidebar_banner_right_max_width = $(window).width() - $('#sidebar_banner_right').offset().left;
		
			if($('#sidebar_banner_right').offset().top > 100)
			{
				sidebar_banner_right_max_height = 0;
				sidebar_banner_right_max_width = 0;
			}
			//console.log(sidebar_banner_right_max_height, sidebar_banner_right_max_width);
			
			
			

			 if(window.innerWidth > 1220 && sidebar_banner_right_max_height > 480 && sidebar_banner_right_max_width > 320) {
				
				if(!((window.innerWidth > 1200 && sidebar_banner_right_max_height > 600 && sidebar_banner_right_max_width > 300)))
				{
					$('.sidebar_banner_placeholder_2').attr('id', 'div-gpt-ad-1628412486954-0');	                
	                googletag.defineSlot('/22218282545/desktop-sidebar', [[320, 480]], 'div-gpt-ad-1628412486954-0').addService(googletag.pubads());
	                
				}
				else
				{
					$('.sidebar_banner_placeholder_2').attr('id', 'div-gpt-ad-1628412486954-0');	                
	                googletag.defineSlot('/22218282545/desktop-sidebar', [[160, 600], [300, 600], [320, 480]], 'div-gpt-ad-1628412486954-0').addService(googletag.pubads());
					
				}
				
                
			} else if(window.innerWidth > 1200 && sidebar_banner_right_max_height > 600 && sidebar_banner_right_max_width > 300) {
				
				$('.sidebar_banner_placeholder_2').attr('id', 'div-gpt-ad-1628412486954-0');	                
                googletag.defineSlot('/22218282545/desktop-sidebar', [[300, 600]], 'div-gpt-ad-1628412486954-0').addService(googletag.pubads());
				
                
			} else if(window.innerWidth > (1200-160) && sidebar_banner_right_max_height > 600 && sidebar_banner_right_max_width > 160) {
				
				$('.sidebar_banner_placeholder_2').attr('id', 'div-gpt-ad-1628412486954-0');	                
                googletag.defineSlot('/22218282545/desktop-sidebar', [[160, 600]], 'div-gpt-ad-1628412486954-0').addService(googletag.pubads());
				
            
			} else if(window.innerWidth > 1095) {
				
				$('.sidebar_banner_placeholder_1').attr('id', 'div-gpt-ad-1628412486954-0');	                
                googletag.defineSlot('/22218282545/desktop-sidebar', [[300, 250], [336, 280]], 'div-gpt-ad-1628412486954-0').addService(googletag.pubads());
				
                
			} else if(window.innerWidth > 1000) {
				
				$('.sidebar_banner_placeholder_1').attr('id', 'div-gpt-ad-1628412486954-0');	                
                googletag.defineSlot('/22218282545/desktop-sidebar', [[300, 250]], 'div-gpt-ad-1628412486954-0').addService(googletag.pubads());
				
                
			}
			
			*/
			
			
			if($('#div-gpt-ad-1628418634269-0').length) {
				googletag.defineSlot('/22218282545/desktop//first-post', [[250, 250], [336, 280], [750, 100], [750, 200], [468, 60], 'fluid', [750, 300], [292, 30], [300, 100], [300, 31], [300, 75], [300, 50], [480, 320], [300, 250], [728, 90]], 'div-gpt-ad-1628417380408-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628417380408-0');
			} 

			if($('#div-gpt-ad-1628418634269-0').length) {
				googletag.defineSlot('/22218282545/desktop//intrapost-1', [[300, 50], [300, 250], [468, 60], [336, 280], [750, 100], 'fluid', [300, 75], [292, 30], [300, 100], [750, 300], [728, 90], [480, 320], [750, 200], [250, 250], [300, 31]], 'div-gpt-ad-1628418634269-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418634269-0');
			} 
			
			if($('#div-gpt-ad-1628418670612-0').length) {
				googletag.defineSlot('/22218282545/desktop//intrapost-2', [[300, 75], [300, 50], [750, 300], [480, 320], [300, 31], [468, 60], [750, 100], 'fluid', [250, 250], [300, 250], [750, 200], [336, 280], [728, 90], [292, 30], [300, 100]], 'div-gpt-ad-1628418670612-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418670612-0');
			} 
			
			if($('#div-gpt-ad-1628418684628-0').length) {
				googletag.defineSlot('/22218282545/desktop//intrapost-3', [[300, 100], [480, 320], [750, 300], [292, 30], [250, 250], 'fluid', [300, 250], [728, 90], [300, 50], [336, 280], [750, 100], [300, 31], [300, 75], [468, 60], [750, 200]], 'div-gpt-ad-1628418684628-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418684628-0');
			} 
			
			if($('#div-gpt-ad-1628418703266-0').length) {
				googletag.defineSlot('/22218282545/desktop//intrapost-4', [[300, 50], [468, 60], [300, 250], [250, 250], [300, 31], 'fluid', [300, 100], [480, 320], [750, 300], [728, 90], [750, 200], [336, 280], [750, 100], [300, 75], [292, 30]], 'div-gpt-ad-1628418703266-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418703266-0');
			} 
			
			if($('#div-gpt-ad-1628418716648-0').length) {
				googletag.defineSlot('/22218282545/desktop//intrapost-5', [[480, 320], [750, 300], [292, 30], [300, 31], [300, 100], [250, 250], [300, 250], [728, 90], 'fluid', [300, 50], [336, 280], [750, 100], [468, 60], [300, 75], [750, 200]], 'div-gpt-ad-1628418716648-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418716648-0');
			} 
			
			if($('#div-gpt-ad-1628418734326-0').length) {
				googletag.defineSlot('/22218282545/desktop//last-post', [[292, 30], [300, 31], [300, 75], [300, 50], [750, 200], [480, 320], [750, 300], [468, 60], [250, 250], [300, 250], [336, 280], [750, 100], 'fluid', [728, 90], [300, 100]], 'div-gpt-ad-1628418734326-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418734326-0');
			} 
			
			if($('#div-gpt-ad-1628418748551-0').length) {
				googletag.defineSlot('/22218282545/desktop//page-end', [[480, 320], 'fluid', [300, 31], [300, 100], [292, 30], [750, 200], [300, 50], [750, 300], [300, 75], [336, 280], [468, 60], [300, 250], [750, 100], [728, 90], [250, 250]], 'div-gpt-ad-1628418748551-0').addService(googletag.pubads());
				//googletag.display('div-gpt-ad-1628418748551-0');
			}
			
			
                /*
            if($('#div-gpt-ad-1613044802223-0').length) {
	            googletag.defineSlot('/2507246/KIV//narkive//desktop//last-post', [[300, 250], [480, 320], [336, 280], [728, 90]], 'div-gpt-ad-1613044802223-0').addService(googletag.pubads());
	            //googletag.display('div-gpt-ad-1613044802223-0');
	        }
            	
             
            if($('#div-gpt-ad-1613044817202-0').length) {
	            googletag.defineSlot('/2507246/KIV//narkive//desktop//simthread', [[300, 250], [728, 90], [480, 320], [336, 280]], 'div-gpt-ad-1613044817202-0').addService(googletag.pubads());
	            //googletag.display('div-gpt-ad-1613044817202-0');
	        }
            
            	*/
				
				
				
				
				var topic = "hash-" + ((nk.conf.thread_hash.charCodeAt(0) + nk.conf.thread_hash.charCodeAt(1) + nk.conf.thread_hash.charCodeAt(2) + nk.conf.thread_hash.charCodeAt(3)) % 25);
				
				
				
			googletag.pubads().enableLazyLoad({
				  // Fetch slots within 5 viewports.
				  fetchMarginPercent: 500,
				  // Render slots within 2 viewports.
				  renderMarginPercent: 200,
				  // Double the above values on mobile, where viewports are smaller
				  // and users tend to scroll faster.
				  mobileScaling: 2.0
				});
				
				googletag.pubads().setTargeting(topic, nk.conf.thread_hash);


				googletag.enableServices();
		
			if(adsense_dispatcher_id != 46) {
					googletag.display('div-gpt-ad-1628442403237-0');
			}
				
				
				
				
				if($('#div-gpt-ad-1628418634269-0').length) {
					googletag.display('div-gpt-ad-1628417380408-0');
				} 
				
				if($('#div-gpt-ad-1628418634269-0').length) {
					googletag.display('div-gpt-ad-1628418634269-0');
				} 
				
				if($('#div-gpt-ad-1628418670612-0').length) {
					googletag.display('div-gpt-ad-1628418670612-0');
				} 
				
				if($('#div-gpt-ad-1628418684628-0').length) {
					googletag.display('div-gpt-ad-1628418684628-0');
				} 
				
				if($('#div-gpt-ad-1628418703266-0').length) {
					googletag.display('div-gpt-ad-1628418703266-0');
				} 
				
				if($('#div-gpt-ad-1628418716648-0').length) {
					googletag.display('div-gpt-ad-1628418716648-0');
				} 
				
				if($('#div-gpt-ad-1628418734326-0').length) {
					googletag.display('div-gpt-ad-1628418734326-0');
				} 
				
				if($('#div-gpt-ad-1628418748551-0').length) {
					googletag.display('div-gpt-ad-1628418748551-0');
				}
				
				
				
				

				//googletag.pubads().disableInitialLoad();
		
			   // googletag.pubads().enableSingleRequest();
			  //  googletag.enableServices();
				
				
				
				//googletag.pubads().refresh();
				
				
				//googletag.display(GAM_web_interstitial);
				
				
				
				
				
				
				
				
				
				
				
				
				if(0) {
				googletag.pubads().addEventListener('slotRenderEnded', function(event) {
					
					
					if(adsense_dispatcher_id != 45 && (event.isEmpty == true && '/22218282545/desktop//sidebar' == event.slot.getSlotId().getName()))
					{
						
						
						
						  if(sidebar_banner_right_max_height > 340 && sidebar_banner_right_max_width > 300) {
							  
							  
								  if(sidebar_banner_right_max_height > 300)
								  sidebar_banner_right_max_height = 300;
								  
								if(sidebar_banner_right_max_width < sidebar_banner_right_max_height * 1.7777777778)
									sidebar_banner_right_max_height = sidebar_banner_right_max_width * 0.5625;
							  
								//$('.sidebar_banner_placeholder_2').css('border', '1px solid red');	 
								//$('#sidebar_banner_right').css('width', sidebar_banner_right_max_width + 'px');	
								$('#sidebar_banner_right').css('height', sidebar_banner_right_max_height + 'px');	 
								$('#sidebar_banner_right').css('width', (sidebar_banner_right_max_height * 1.7777777778) + 'px');	  
								
								$('.sidebar_banner_placeholder_2').css('height', sidebar_banner_right_max_height + 'px');	 
								$('.sidebar_banner_placeholder_2').css('width', (sidebar_banner_right_max_height * 1.7777777778) + 'px');	  
								
								$('.sidebar_banner_placeholder_2').attr('id', nk.playbuzz_id);
								$('.sidebar_banner_placeholder_2').attr('onimpression', 'nk_playbuzz_impression');
								
								$('.sidebar_banner_placeholder_2').prop('id', nk.playbuzz_id);
								$('.sidebar_banner_placeholder_2').prop('onimpression', 'nk_playbuzz_impression');
								
								(function (d, s, n) { var js, fjs = d.getElementsByTagName(s)[0]; js = d.createElement(s); js.className = n; js.src = "//player.ex.co/player/" + nk.playbuzz_id; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'exco-player'));
		
								
								/*
								<div style="width: 550px; margin-left: 12px">
								<div style="margin-top: 16px; margin-bottom: 16px; height: 315px;" onimpression="nk_playbuzz_impression" id="<?php echo $GLOBALS['playbuzz_id'] ?>"></div>
								</div>
								
								*/
								
								 
							  
						  } else {
							  
							  //$('.sidebar_banner_placeholder_1').css('width', sidebar_banner_max_width + 'px');	
							  //$('.sidebar_banner_placeholder_1').css('height', sidebar_banner_max_height + 'px');	  
							  
							  $('#sidebar_banner_placeholder_1_outer').css('width', sidebar_banner_max_width + 'px');	
							  $('#sidebar_banner_placeholder_1_outer').css('height', (sidebar_banner_max_width * 0.5625) + 'px');	  
							  
							  $('.sidebar_banner_placeholder_1').attr('id', nk.playbuzz_id);
							  $('.sidebar_banner_placeholder_1').attr('onimpression', 'nk_playbuzz_impression');
							  
							  $('.sidebar_banner_placeholder_1').prop('id', nk.playbuzz_id);
								$('.sidebar_banner_placeholder_1').prop('onimpression', 'nk_playbuzz_impression');
							  
							  if(sidebar_banner_max_width == 340)
								$('#sidebar_banner_placeholder_1_outer').css('margin-left', '-100px');
							else
								$('#sidebar_banner_placeholder_1_outer').css('margin-left', '0');
								
								
								(function (d, s, n) { var js, fjs = d.getElementsByTagName(s)[0]; js = d.createElement(s); js.className = n; js.src = "//player.ex.co/player/" + nk.playbuzz_id; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'exco-player'));
								
						  }
						  
						  
						  
						  
					}
					
					
				});
				}
				
				/*
				googletag.pubads().addEventListener('impressionViewable', function(event) {
					nk.setRefreshSlotTimeout(event);
				});
				
				googletag.pubads().addEventListener('slotVisibilityChanged',
					function(event) {
					  
					  if(event.inViewPercentage < 50)
					  	return;
					  
					  for(i = 0; i < nk.ads_refreshable_queue.length; i++)  {
						  if(nk.ads_refreshable_queue[i].getSlotElementId() == event.slot.getSlotElementId()) {
							  googletag.pubads().refresh([event.slot]);
							  console.log(event.slot.getSlotId().getName() + " refreshed because in view as of %: " + event.inViewPercentage);
							  nk.ads_refreshable_queue.splice(i, 1);
							  break;
						  }
					  }
				  }
				);
                */
            

        }


        // mobile
        if (1 && adsense_dispatcher_id == 44) {
			
			
			if($('#div-gpt-ad-1628528438452-0').length) {
				//googletag.defineSlot('/22218282545/mobile//first-post', [[320, 100], 'fluid', [320, 50]], 'div-gpt-ad-1628528438452-0').addService(googletag.pubads());
				googletag.defineSlot('/22218282545/mobile//first-post', ['fluid'], 'div-gpt-ad-1628528438452-0').addService(googletag.pubads());

			}
			
			/*
			if($('#div-gpt-ad-1628528454385-0').length) {
googletag.defineSlot('/22218282545/mobile//last-post', ['fluid', [320, 100], [300, 50]], 'div-gpt-ad-1628528454385-0').addService(googletag.pubads());
			}
			
			
			if($('#div-gpt-ad-1628528456095-0').length) {
googletag.defineSlot('/22218282545/mobile//page-end', ['fluid', [320, 100], [300, 50]], 'div-gpt-ad-1628528456095-0').addService(googletag.pubads());
			}
			
			
			if($('#div-gpt-ad-1628528440923-0').length) {
googletag.defineSlot('/22218282545/mobile//intrapost-1', [[320, 100], [300, 50], 'fluid'], 'div-gpt-ad-1628528440923-0').addService(googletag.pubads());
			}
			

			if($('#div-gpt-ad-1628528442481-0').length) {
googletag.defineSlot('/22218282545/mobile//intrapost-2', [[320, 100], [300, 50], 'fluid'], 'div-gpt-ad-1628528442481-0').addService(googletag.pubads());
			}
			
			
			if($('#div-gpt-ad-1628528448499-0').length) {
googletag.defineSlot('/22218282545/mobile//intrapost-3', [[320, 100], 'fluid', [300, 50]], 'div-gpt-ad-1628528448499-0').addService(googletag.pubads());
			}
			
			
			if($('#div-gpt-ad-1628528450716-0').length) {
googletag.defineSlot('/22218282545/mobile//intrapost-4', [[300, 50], [320, 100], 'fluid'], 'div-gpt-ad-1628528450716-0').addService(googletag.pubads());
			}
			
			
			if($('#div-gpt-ad-1628528452564-0').length) {
googletag.defineSlot('/22218282545/mobile//intrapost-5', [[300, 50], 'fluid', [320, 100]], 'div-gpt-ad-1628528452564-0').addService(googletag.pubads());
			}
			*/


			var topic = "hash-" + ((nk.conf.thread_hash.charCodeAt(0) + nk.conf.thread_hash.charCodeAt(1) + nk.conf.thread_hash.charCodeAt(2) + nk.conf.thread_hash.charCodeAt(3)) % 25);

			
			googletag.pubads().enableLazyLoad({
				  // Fetch slots within 5 viewports.
				  fetchMarginPercent: 500,
				  // Render slots within 2 viewports.
				  renderMarginPercent: 200,
				  // Double the above values on mobile, where viewports are smaller
				  // and users tend to scroll faster.
				  mobileScaling: 2.0
				});
				
				googletag.pubads().setTargeting(topic, nk.conf.thread_hash);
				
				googletag.enableServices();
				
			if($('#div-gpt-ad-1628528438452-0').length) {
				googletag.display('div-gpt-ad-1628528438452-0');	
			}
			
			/*
			
		
			if($('#div-gpt-ad-1628528454385-0').length) {
				googletag.display('div-gpt-ad-1628528454385-0');	
			}
			
			
			if($('#div-gpt-ad-1628528456095-0').length) {
				googletag.display('div-gpt-ad-1628528456095-0');	
			}
			
			
			if($('#div-gpt-ad-1628528440923-0').length) {
				googletag.display('div-gpt-ad-1628528440923-0');	
			}
			

			if($('#div-gpt-ad-1628528442481-0').length) {
				googletag.display('div-gpt-ad-1628528442481-0');	
			}
			
			
			if($('#div-gpt-ad-1628528448499-0').length) {
				googletag.display('div-gpt-ad-1628528448499-0');	
			}
			
			
			if($('#div-gpt-ad-1628528450716-0').length) {
				googletag.display('div-gpt-ad-1628528450716-0');	
			}
			
			
			if($('#div-gpt-ad-1628528452564-0').length) {
				googletag.display('div-gpt-ad-1628528452564-0');	
			}
			
			*/
			
		
			
		}
		
		
		//googletag.pubads().enableSingleRequest();
		
		
		
		
		
        
    });
    
    
    /*
    googletag.cmd.push(function() {
		
		googletag.pubads().addEventListener('slotRenderEnded', function(event) { // slotOnload doesn't have event.size param 
				
			if('div-gpt-ad-1613045470503-0' == event.slot.getSlotElementId())
			{
				//console.log(event);
				//console.log(event.size);
				
				//console.log(event.slot.getSizes());
				
				if(event.size !== null && event.size[1] < 130)
				{
					$('#sticky_banner_shadow').css('display', 'block');
					
					
					$('#sticky_banner_shadow').css('height', event.size[1] + 'px');
					$('#sticky_banner').css('height', event.size[1] + 'px');
					$('#sticky_banner').addClass('sticky_banner_visible_border');
					
					//$('#div-gpt-ad-1610367155343-0').css('margin-left', '0px !important');
					
					
					$('#footer_wrapper').css('height', (event.size[1] + 30) + 'px');
					
					
				}
			}
			
			else if('div-gpt-ad-1610365717614-0' == event.slot.getSlotElementId())
			{
				if(event.size !== null && event.size[1] < 130)
				{
					
				}
			}
			
			else if('div-gpt-ad-1613044817202-0' == event.slot.getSlotElementId() && event.size !== null)
			{
				//console.log(event.size);
				if(event.size[0] > 700)
				{
					//console.log('margin-left null');
					$('#div-gpt-ad-1613044817202-0').css('margin-left', '0px');
				}
				else
				{
					//console.log('margin-left 18');
					$('#div-gpt-ad-1613044817202-0').css('margin-left', '18px');
				}
			}
			
			else if('div-gpt-ad-1613044802223-0' == event.slot.getSlotElementId() && event.size !== null)
			{
				//console.log(event.size);
				if(event.size[0] > 700)
				{
					//console.log('margin-left null');
					$('#div-gpt-ad-1613044802223-0').css('margin-left', '0px');
				}
				else
				{
					//console.log('margin-left 18');
					$('#div-gpt-ad-1613044802223-0').css('margin-left', '18px');
				}
			}
			
				
	        
	        
	        
	        
	        
		});
		
		
		
		//googletag.pubads().addEventListener('slotRenderEnded', function(event) {
		//  setRefreshSlotTimeout(event.slot);
		//});
		
		//googletag.pubads().addEventListener('impressionViewable', function(event) {
		//  setRefreshSlotTimeout(event.slot);
		//});
		
		
		

				
	});
	*/
	
	
	
	
	
	});

	//function adsBlocked(callback){
	  var testURL = 'https://player.avplayer.com/script/2/2.55/libs/hls.min.js'
	
	  var myInit = {
	    method: 'HEAD',
	    mode: 'no-cors'
	  };
	
	  var myRequest = new Request(testURL, myInit);
	
	  fetch(myRequest).then(function(response) {
	    return response;
	  }).then(function(response) {
	    //console.log(response);
	    //callback(false)
	  }).catch(function(e){
	    nk.adb = 'enabled';
	  });
	//}


	
	
	nk.adb = 'disabled';
	
	
	
	
	