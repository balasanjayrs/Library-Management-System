document.addEventListener('DOMContentLoaded', () => {
  const issueDate = document.querySelector('#issueDate');
  const dueDate = document.querySelector('#dueDate');
  if (issueDate && dueDate) {
    const update = () => {
      if (!issueDate.value) { dueDate.value = ''; return; }
      const d = new Date(issueDate.value + 'T12:00:00');
      d.setDate(d.getDate() + 14);
      dueDate.value = d.toISOString().slice(0, 10);
    };
    issueDate.addEventListener('change', update);
  }
  document.querySelectorAll('.alert').forEach((alert) => {
    setTimeout(() => alert.classList.add('fade'), 5500);
  });
});
