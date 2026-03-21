// ===== Main JavaScript ====

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips and popovers
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Form validation
    validateForms();

    // Add animation on scroll
    observeElements();

    // Close alerts after 5 seconds
    dismissAlerts();
});

// ===== Form Validation =====
function validateForms() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// ===== Scroll Animation =====
function observeElements() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in');
                observer.unobserve(entry.target);
            }
        });
    });

    document.querySelectorAll('.feature-card, .vehicle-card').forEach(el => {
        observer.observe(el);
    });
}

// ===== Auto Dismiss Alerts =====
function dismissAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000); // 5 seconds
    });
}

// ===== Utility Functions =====

// Format currency
function formatCurrency(value) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
}

// Show loading state
function showLoading(element) {
    element.classList.add('loading');
    element.disabled = true;
}

// Hide loading state
function hideLoading(element) {
    element.classList.remove('loading');
    element.disabled = false;
}

// Show notification
function showNotification(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show fixed-top m-2`;
    alertDiv.style.zIndex = '9999';
    alertDiv.style.maxWidth = '400px';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertDiv);
        bsAlert.close();
    }, 3000);
}

// Validate email
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Validate password strength
function validatePasswordStrength(password) {
    const strength = {
        weak: password.length >= 6,
        medium: password.length >= 8 && /[A-Z]/.test(password),
        strong: password.length >= 12 && /[A-Z]/.test(password) && /[0-9]/.test(password)
    };
    
    if (strength.strong) return 'strong';
    if (strength.medium) return 'medium';
    if (strength.weak) return 'weak';
    return 'very-weak';
}

// ===== Search Functionality =====
function searchVehicles(vehicleType, brand, maxPrice) {
    const params = new URLSearchParams();
    
    if (vehicleType) params.append('vehicleType', vehicleType);
    if (brand) params.append('brand', brand);
    if (maxPrice) params.append('maxPrice', maxPrice);
    
    window.location.href = `/vehicles/search?${params.toString()}`;
}

// ===== Cart Functions (Future) =====
function addToCart(vehicleId) {
    showNotification('Xe đã được thêm vào giỏ hàng!', 'success');
}

function removeFromCart(vehicleId) {
    showNotification('Xe đã được xóa khỏi giỏ hàng!', 'info');
}

// ===== Logout =====
function logout() {
    if (confirm('Bạn chắc chắn muốn đăng xuất?')) {
        window.location.href = '/logout';
    }
}

console.log('EV Rental System loaded successfully!');
