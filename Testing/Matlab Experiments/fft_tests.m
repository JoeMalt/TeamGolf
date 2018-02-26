fft_original  = fft(test3_original);
fft_modified = fft(test3_modified); 

figure
plot(abs(fft_original), 'b')
hold on
plot(abs(fft_modified), 'r')
%hold off


fft_original  = fft(test3original_blur);
fft_modified = fft(test3scanblur); 
figure
plot(abs(fft_original), 'k')
hold on
plot(abs(fft_modified), 'g')
hold off