function cost = objfun(x)

% x(3) - y scaling
% x(2) - translation
% x(1) - x scaling
load signals_thresholded.mat
 
nOriginalItems = length(original); 
nScanItems = length(scan); 

%scan_scaled = scan * x(3); 
%A = NaN(1, nOriginalItems); 
transform = @(y)(floor(min(max(x(1)*y + x(2), 1), nOriginalItems))); 
area_original = 0.0; 
area_scan = 0.0; 


area = 0.0; 
for index = 1:min(nOriginalItems, nScanItems)
    %disp(index)
    %disp(transform(index))
    
    scan_height = 0.0; 
    if transform(index) <= nScanItems
        scan_height = scan(transform(index)) * x(1) * x(3); 
    end
    area = area + (original(index) - scan_height)^2; 
end
%for index = 1:nOriginalItems
%    area_original = area_original + original(index) ; 
%end
%for index = 1:nScanItems
%    area_scan = area_scan + scan(transform(index)) * x(1) * x(3) ; 
%end 
   
%for index = 1:nOriginalItem:
%    new_index = floor(transform(index)); 
%    if((new_index >= 1) && (new_index <= nScanItems))
%        errors = errors + (scan_scaled(new_index) - original(index))^2; 
%    end
%end

%result = -sum( A.*denoised_original , 'omitnan')


% Take the covariance over all the non-NaN items in 


% Minimise negative covariance
%EX = mean(original, 'omitnan') 
%EY = mean(A, 'omitnan') 
%EXEXYEY = mean((original - EX).*(A - EY), 'omitnan')
%error = (area_scan - area_original)^2
error = area 

cost = error;




